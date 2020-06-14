package org.jhapy.notification.task;

import org.jhapy.notification.service.MailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Service
@Transactional
public class EmailQueueTask {

  private final MailService mailService;

  public EmailQueueTask(
      MailService mailService) {
    this.mailService = mailService;
  }

  @Scheduled(cron = "${jhapy.tasks.emailQueueCronExpression}")
  public void processEmailQueue() {
    mailService.processNotSentEmails();
  }
}