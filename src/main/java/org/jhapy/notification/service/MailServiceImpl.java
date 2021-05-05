package org.jhapy.notification.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailStatusEnum;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.repository.MailMessageRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
@Service
@Transactional(readOnly = true)
public class MailServiceImpl implements MailService, HasLogger {

  private final JavaMailSender emailSender;

  private final MailMessageRepository mailMessageRepository;

  private final MailTemplateService mailTemplateService;

  public MailServiceImpl(JavaMailSender emailSender,
      MailMessageRepository mailMessageRepository,
      MailTemplateService mailTemplateService) {
    this.emailSender = emailSender;
    this.mailMessageRepository = mailMessageRepository;
    this.mailTemplateService = mailTemplateService;
  }

  @Override
  public Page<Mail> findAnyMatching(String filter, Pageable pageable) {
    var loggerString = getLoggerPrefix("findAnyMatching");
    debug(loggerString, "In = {0}", filter);
    Page<Mail> result;

    if (StringUtils.isNotBlank(filter)) {
      result = mailMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = mailMessageRepository.findAll(pageable);
    }

    debug(loggerString, "Out = {0}", result);

    return result;
  }


  @Override
  public long countAnyMatching(String filter) {
    var loggerString = getLoggerPrefix("countAnyMatching");
    debug(loggerString, "In = {0}", filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = mailMessageRepository.countByCriteria(filter);
    } else {
      result = mailMessageRepository.count();
    }

    debug(loggerString, "Out = {0}", result);
    return result;
  }

  @Override
  public void sendSimpleMail(String to, String subject, String text) {
    var message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

  @Transactional
  @Override
  public MailStatusEnum sendEmail(String to, String emailAction, Map<String, String> attributes,
      Map<String, byte[]> attachments, String iso3Language) {
    var loggerPrefix = getLoggerPrefix("sendEmail");

    var _mailTemplate = mailTemplateService
        .findByMailAction(emailAction, iso3Language);

    if (_mailTemplate.isPresent()) {
      var template = _mailTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);

      if (StringUtils.isNotBlank(to)) {
        return sendAndSave(to, template, attributes, attachments);
      } else {
        warn(loggerPrefix, "No email to send.");
      }
    } else {
      error(loggerPrefix, "Template not found = {0}", emailAction);
    }
    return null;
  }

  private MailStatusEnum sendAndSave(String to, MailTemplate mailTemplate,
      Map<String, String> attributes,
      Map<String, byte[]> attachments) {
    var loggerPrefix = getLoggerPrefix("sendAndSave");
    trace(loggerPrefix, "Template = {0}, attributes = {1}", mailTemplate, attributes);
    var mailMessage = new Mail();
    mailMessage.setMailStatus(MailStatusEnum.NOT_SENT);
    try {
      debug(loggerPrefix, "Building the message...");

      var bodyTemplate = new Template(
          null,
          mailTemplate.getBody(),
          new Configuration(Configuration.VERSION_2_3_28)
      );

      var body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);

      var subjectTemplate = new Template(
          null,
          mailTemplate.getSubject(),
          new Configuration(Configuration.VERSION_2_3_28)
      );

      var subject = FreeMarkerTemplateUtils
          .processTemplateIntoString(subjectTemplate, attributes);

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
      error(loggerPrefix, "Error while preparing mail = {0}, message = {1}",
          mailMessage.getMailAction(), e.getMessage());
      mailMessage.setMailStatus(MailStatusEnum.ERROR);
    } finally {
      debug(loggerPrefix, "Email sent status = {0}", mailMessage.getMailStatus());
    }
    mailMessage = mailMessageRepository.save(mailMessage);

    return mailMessage.getMailStatus();
  }

  private MailStatusEnum sendMail(Mail mail) {
    var loggerPrefix = getLoggerPrefix("sendMailMessage");
    try {
      var message = emailSender.createMimeMessage();
      var helper = new MimeMessageHelper(message,
          MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
          StandardCharsets.UTF_8.name());
      helper.setFrom(mail.getFrom());
      helper.setTo(mail.getTo());
      if (StringUtils.isNotBlank(mail.getCopyTo())) {
        helper.setCc(mail.getCopyTo());
      }
      helper.setSubject(mail.getSubject());
      helper.setText(mail.getBody(), true);
      if (mail.getAttachements() != null) {
        for (String attachment : mail.getAttachements().keySet()) {
          helper.addAttachment(attachment,
              new ByteArrayResource(mail.getAttachements().get(attachment)));
        }
      }
      debug(loggerPrefix, "Sending...");
      emailSender.send(message);
      mail.setErrorMessage(null);
      mail.setMailStatus(MailStatusEnum.SENT);
    } catch (MailException | MessagingException mailException) {
      error(loggerPrefix, "Error while sending mail = {0}, message = {1}", mail.getMailAction(),
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
    mail = mailMessageRepository.save(mail);
    return mail.getMailStatus();
  }

  @Override
  @Transactional
  public void processNotSentEmails() {
    var unsentEmails = mailMessageRepository
        .findByMailStatusIn(MailStatusEnum.NOT_SENT, MailStatusEnum.RETRYING);
    unsentEmails.forEach(mail -> {
      sendMail(mail);
      mailMessageRepository.save(mail);
    });
  }

  @Override
  public MongoRepository<Mail, String> getRepository() {
    return mailMessageRepository;
  }
}
