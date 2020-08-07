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
  @RabbitListener(queues = "cloudNotification")
  public void onNewCloudNotificationMessage(
      final CloudNotificationMessage cloudNotificationMessage) {
    String loggerPrefix = getLoggerPrefix("onNewCloudNotificationMessage");

    logger()
        .info(loggerPrefix + "Receiving a request from {} for sending cloud notification {} to {} ",
            cloudNotificationMessage.getApplicationName(), cloudNotificationMessage.getTitle(),
            cloudNotificationMessage.getDeviceToken());
    CloudNotificationMessageStatusEnum result = cloudNotificationMessageService
        .sendCloudNotificationMessage(cloudNotificationMessage.getDeviceToken(),
            cloudNotificationMessage.getCloudNotificationMessageAction(),
            cloudNotificationMessage.getTitle(), cloudNotificationMessage.getBody(),
            cloudNotificationMessage.getData(), cloudNotificationMessage.getAttributes(),
            cloudNotificationMessage.getIso3Language());
    logger().info(loggerPrefix + "Cloud Notification Message status {} ", result);
  }
}
