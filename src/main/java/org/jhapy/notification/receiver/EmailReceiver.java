package org.jhapy.notification.receiver;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.notification.Mail;
import org.jhapy.notification.domain.MailStatusEnum;
import org.jhapy.notification.service.MailService;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class EmailReceiver implements HasLogger {

  private final MailService mailService;

  public EmailReceiver(MailService mailService) {
    this.mailService = mailService;
  }

  /**
   * Automatically launched by JMS broker when new email is received on <strong>mailbox</strong>
   * channel.
   *
   * @param mail the received email
   * @see Mail
   */
  @JmsListener(destination = "mailbox")
  public void onNewMail(final Mail mail) {
    String loggerPrefix = getLoggerPrefix("onNewMail");
    logger().info(loggerPrefix + "Receiving a request from {} for sending email {} to {} ",
        mail.getApplicationName(), mail.getSubject(), mail.getTo());
    MailStatusEnum result = mailService
        .sendEmail(mail.getTo(), mail.getMailAction(),
            mail.getAttributes(), mail.getAttachements(),
            mail.getIso3Language());
    logger().info(loggerPrefix + "Sms status {} ", result);
  }
}
