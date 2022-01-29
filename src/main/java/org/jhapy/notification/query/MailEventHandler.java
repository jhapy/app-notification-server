package org.jhapy.notification.query;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.MailCreatedEvent;
import org.jhapy.cqrs.event.notification.MailDeletedEvent;
import org.jhapy.cqrs.event.notification.MailUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingMailQuery;
import org.jhapy.cqrs.query.notification.GetMailByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.converter.MailConverter;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailStatusEnum;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.repository.MailMessageRepository;
import org.jhapy.notification.repository.MailTemplateRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
@RequiredArgsConstructor
@ProcessingGroup("mail-group")
public class MailEventHandler implements HasLogger {
  private final MailMessageRepository repository;
  private final MailTemplateRepository mailTemplateRepository;
  private final MailConverter converter;
  private final QueryUpdateEmitter queryUpdateEmitter;
  private final JavaMailSender emailSender;

  @ExceptionHandler
  public void handleException(Exception ex) throws Exception {
    String loggerPrefix = getLoggerPrefix("handleException");
    error(
        loggerPrefix,
        ex,
        "Exception in EventHandler (ExceptionHandler): {0}:{1}",
        ex.getClass().getName(),
        ex.getMessage());
    throw ex;
  }

  @EventHandler
  public void on(MailCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onMailCreatedEvent");

    Mail entity = converter.toEntity(event);
    entity = repository.save(entity);

    var optMailTemplate =
        mailTemplateRepository.findByMailActionAndIso3LanguageAndIsActiveIsTrue(
            event.getMailAction(), event.getIso3Language());

    if (optMailTemplate.isPresent()) {
      var template = optMailTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);

      if (StringUtils.isNotBlank(event.getTo())) {
        sendAndSave(event.getTo(), template, event.getAttributes(), event.getAttachments());

        queryUpdateEmitter.emit(
            GetMailByIdQuery.class, query -> true, converter.asDTO(entity, null));

        queryUpdateEmitter.emit(
            CountAnyMatchingMailQuery.class, query -> true, new CountChangeResult());
      } else {
        warn(loggerPrefix, "No email to send.");
      }
    } else {
      error(loggerPrefix, "Template not found = {0}", event.getMailAction());
    }
  }

  @EventHandler
  public void on(MailUpdatedEvent event) throws Exception {
    Mail entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(GetMailByIdQuery.class, query -> true, converter.asDTO(entity, null));
  }

  @EventHandler
  public void on(MailDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }

  private MailStatusEnum sendAndSave(
      String to,
      MailTemplate mailTemplate,
      Map<String, String> attributes,
      Map<String, byte[]> attachments) {
    var loggerPrefix = getLoggerPrefix("sendAndSave");
    trace(loggerPrefix, "Template = {0}, attributes = {1}", mailTemplate, attributes);
    var mailMessage = new Mail();
    mailMessage.setMailStatus(MailStatusEnum.NOT_SENT);
    try {
      debug(loggerPrefix, "Building the message...");

      var bodyTemplate =
          new Template(
              null, mailTemplate.getBody(), new Configuration(Configuration.VERSION_2_3_28));

      var body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);

      var subjectTemplate =
          new Template(
              null, mailTemplate.getSubject(), new Configuration(Configuration.VERSION_2_3_28));

      var subject = FreeMarkerTemplateUtils.processTemplateIntoString(subjectTemplate, attributes);

      // initialize saved mail data
      mailMessage.setFrom(mailTemplate.getFrom());
      mailMessage.setTo(to);
      if (StringUtils.isNotBlank(mailTemplate.getCopyTo())) {
        mailMessage.setCopyTo(mailTemplate.getCopyTo());
      }
      mailMessage.setSubject(subject);
      mailMessage.setBody(body);

      sendMail(mailMessage);
    } catch (Exception e) {
      error(
          loggerPrefix,
          "Error while preparing mail = {0}, message = {1}",
          mailMessage.getMailAction(),
          e.getMessage());
      mailMessage.setMailStatus(MailStatusEnum.ERROR);
    } finally {
      debug(loggerPrefix, "Email sent status = {0}", mailMessage.getMailStatus());
    }
    mailMessage = repository.save(mailMessage);

    return mailMessage.getMailStatus();
  }

  private MailStatusEnum sendMail(Mail mail) {
    var loggerPrefix = getLoggerPrefix("sendMailMessage");
    try {
      var message = emailSender.createMimeMessage();
      var helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());
      helper.setFrom(mail.getFrom());
      helper.setTo(mail.getTo());
      if (StringUtils.isNotBlank(mail.getCopyTo())) {
        helper.setCc(mail.getCopyTo());
      }
      helper.setSubject(mail.getSubject());
      helper.setText(mail.getBody(), true);
      if (mail.getAttachments() != null) {
        for (String attachment : mail.getAttachments().keySet()) {
          helper.addAttachment(
              attachment, new ByteArrayResource(mail.getAttachments().get(attachment)));
        }
      }
      debug(loggerPrefix, "Sending...");
      emailSender.send(message);
      mail.setErrorMessage(null);
      mail.setMailStatus(MailStatusEnum.SENT);
    } catch (MailException | MessagingException mailException) {
      error(
          loggerPrefix,
          "Error while sending mail = {0}, message = {1}",
          mail.getMailAction(),
          mailException.getMessage());
      if (mail.getNbRetry() >= 3) {
        mail.setErrorMessage(mailException.getMessage());
        mail.setMailStatus(MailStatusEnum.ERROR);
      } else {
        mail.setErrorMessage(mailException.getMessage());
        mail.setNbRetry(mail.getNbRetry() + 1);
        mail.setMailStatus(MailStatusEnum.RETRYING);
      }
    }
    mail = repository.save(mail);
    return mail.getMailStatus();
  }

  @Scheduled(cron = "${jhapy.tasks.emailQueueCronExpression}")
  public void processNotSentEmails() {
    var unsentEmails =
        repository.findByMailStatusIn(MailStatusEnum.NOT_SENT, MailStatusEnum.RETRYING);
    unsentEmails.forEach(
        mail -> {
          sendMail(mail);
          repository.save(mail);
        });
  }
}
