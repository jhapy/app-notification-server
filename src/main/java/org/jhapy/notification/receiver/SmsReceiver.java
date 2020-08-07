package org.jhapy.notification.receiver;

import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.notification.Sms;
import org.jhapy.notification.domain.SmsStatusEnum;
import org.jhapy.notification.service.SmsService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@Component
public class SmsReceiver implements HasLogger {

  private final SmsService smsService;

  public SmsReceiver(SmsService smsService) {
    this.smsService = smsService;
  }

  /**
   * Automatically launched by JMS broker when new sms is received on <strong>sms</strong> channel.
   *
   * @param sms the received email
   * @see Sms
   */
  @RabbitListener(queues = "sms")
  public void onNewSms(final Sms sms) {
    String loggerPrefix = getLoggerPrefix("onNewSms");
    logger().info(loggerPrefix + "Receiving a request from {} for sending sms to {} ",
        sms.getApplicationName(), sms.getPhoneNumber());
    SmsStatusEnum result = smsService
        .sendSms(sms.getPhoneNumber(), sms.getSmsAction(), sms.getAttributes(),
            sms.getIso3Language());
    logger().info(loggerPrefix + "Sms status {} ", result);
  }
}
