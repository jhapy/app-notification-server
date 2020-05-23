package org.jhapy.notification.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang3.StringUtils;
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
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailStatusEnum;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.repository.MailMessageRepository;

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
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<Mail> result;

    if (StringUtils.isNotBlank(filter)) {
      result = mailMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = mailMessageRepository.findAll(pageable);
    }

    logger().debug(loggerString + "Out = " + result);

    return result;
  }


  @Override
  public long countAnyMatching(String filter) {
    String loggerString = getLoggerPrefix("countAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = mailMessageRepository.countByCriteria(filter);
    } else {
      result = mailMessageRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  public void sendSimpleMail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

  @Transactional
  @Override
  public MailStatusEnum sendEmail(String to, String emailAction, Map<String, String> attributes,
      Map<String, byte[]> attachments, String iso3Language) {
    String loggerPrefix = getLoggerPrefix("sendEmail");

    Optional<MailTemplate> _mailTemplate = mailTemplateService
        .findByMailAction(emailAction, iso3Language);

    if (_mailTemplate.isPresent()) {
      MailTemplate template = _mailTemplate.get();
      logger().trace(loggerPrefix + "Template found = {}", template);

      if (StringUtils.isNotBlank(to)) {
        return sendAndSave(to, template, attributes, attachments);
      } else {
        logger().warn(loggerPrefix + "No email to send.");
      }
    }
    return null;
  }

  private MailStatusEnum sendAndSave(String to, MailTemplate mailTemplate,
      Map<String, String> attributes,
      Map<String, byte[]> attachments) {
    String loggerPrefix = getLoggerPrefix("sendAndSave");
    logger().trace(loggerPrefix + "Template = {}, attributes = {}", mailTemplate, attributes);
    Mail mailMessage = new Mail();
    mailMessage.setMailStatus(MailStatusEnum.NOT_SENT);
    try {
      logger().debug(loggerPrefix + "Building the message...");

      Template bodyTemplate = new Template(
          null,
          mailTemplate.getBody(),
          new Configuration(Configuration.VERSION_2_3_28)
      );

      String body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);

      Template subjectTemplate = new Template(
          null,
          mailTemplate.getSubject(),
          new Configuration(Configuration.VERSION_2_3_28)
      );

      String subject = FreeMarkerTemplateUtils
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
      logger().error(loggerPrefix + "Error while preparing mail = {}, message = {}",
          mailMessage.getMailAction(), e.getMessage());
      mailMessage.setMailStatus(MailStatusEnum.ERROR);
    } finally {
      logger().debug(loggerPrefix + "Email sent status = {}", mailMessage.getMailStatus());
    }
    mailMessage = mailMessageRepository.save(mailMessage);

    return mailMessage.getMailStatus();
  }

  private MailStatusEnum sendMail(Mail mail) {
    String loggerPrefix = getLoggerPrefix("sendMailMessage");
    try {
      MimeMessage message = emailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message,
          MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
          StandardCharsets.UTF_8.name());
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
      logger().debug(loggerPrefix + "Sending...");
      emailSender.send(message);
      mail.setErrorMessage(null);
      mail.setMailStatus(MailStatusEnum.SENT);
    } catch (MailException | MessagingException mailException) {
      logger()
          .error(loggerPrefix + "Error while sending mail = {}, message = {}", mail.getMailAction(),
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
    List<Mail> unsentEmails = mailMessageRepository
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
