package org.jhapy.notification.receiver;

import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.notification.CloudNotificationMessage;
import org.jhapy.dto.domain.notification.Mail;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.jhapy.notification.service.CloudNotificationMessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class CloudNotificationMessageReceiver implements HasLogger {

  private final CloudNotificationMessageService cloudNotificationMessageService;

  public CloudNotificationMessageReceiver(
      CloudNotificationMessageService cloudNotificationMessageService) {
    this.cloudNotificationMessageService = cloudNotificationMessageService;
  }

  /**
   * Automatically launched by JMS broker when new email is received on
   * <strong>cloudNotification</strong> channel.
   *
   * @param cloudNotificationMessage the received notification
   * @see Mail
   */
  @RabbitListener(queues = "#{cloudNotificationQueue.name}")
  public void onNewCloudNotificationMessage(
      final CloudNotificationMessage cloudNotificationMessage) {
    var loggerPrefix = getLoggerPrefix("onNewCloudNotificationMessage");

    info(loggerPrefix, "Receiving a request from {0} for sending cloud notification {1} to {2}",
        cloudNotificationMessage.getApplicationName(), cloudNotificationMessage.getTitle(),
        cloudNotificationMessage.getDeviceToken());
    CloudNotificationMessageStatusEnum result = cloudNotificationMessageService
        .sendCloudNotificationMessage(cloudNotificationMessage.getDeviceToken(),
            cloudNotificationMessage.getCloudNotificationMessageAction(),
            cloudNotificationMessage.getTitle(), cloudNotificationMessage.getBody(),
            cloudNotificationMessage.getData(), cloudNotificationMessage.getAttributes(),
            cloudNotificationMessage.getIso3Language());
    info(loggerPrefix, "Cloud Notification Message status {0}", result);
  }
}
