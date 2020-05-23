package org.jhapy.notification.service;


import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailStatusEnum;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
public interface MailService extends BaseCrudService<Mail> {

  Page<Mail> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  void sendSimpleMail(String to, String subject, String text);

  MailStatusEnum sendEmail(String to, String emailAction, Map<String, String> attributes,
      Map<String, byte[]> attachments, String iso3Language);

  void processNotSentEmails();
}
