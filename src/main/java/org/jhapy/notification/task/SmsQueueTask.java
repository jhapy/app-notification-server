package org.jhapy.notification.task;

import org.jhapy.notification.service.SmsService;
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
public class SmsQueueTask {

  private final SmsService smsService;

  public SmsQueueTask(
      SmsService smsService) {
    this.smsService = smsService;
  }

  @Scheduled(cron = "${jhapy.tasks.smsQueueCronExpression}")
  public void processSmsQueue() {
    smsService.processNotSentSms();
  }
}