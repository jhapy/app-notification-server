package org.jhapy.notification.receiver;

import org.jhapy.commons.utils.HasLogger;
import org.jhapy.dto.domain.notification.CloudDataMessage;
import org.jhapy.dto.domain.notification.Mail;
import org.jhapy.notification.domain.CloudDataMessageStatusEnum;
import org.jhapy.notification.service.CloudDataMessageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-03
 */
@Component
public class CloudDataMessageReceiver implements HasLogger {

  private final CloudDataMessageService cloudDataMessageService;

  public CloudDataMessageReceiver(
      CloudDataMessageService cloudDataMessageService) {
    this.cloudDataMessageService = cloudDataMessageService;
  }

  /**
   * Automatically launched by JMS broker when new email is received on <strong>cloudData</strong>
   * channel.
   *
   * @param cloudDataMessage the received notification
   * @see Mail
   */
  @RabbitListener(queues = "#{cloudDataQueue.name}")
  public void onNewCloudDataMessage(final CloudDataMessage cloudDataMessage) {
    String loggerPrefix = getLoggerPrefix("onNewCloudDataMessage");

    logger().info(loggerPrefix + "Receiving a request from {} for sending cloud data {} to {} ",
        cloudDataMessage.getApplicationName(), cloudDataMessage.getData(),
        cloudDataMessage.getDeviceToken());
    CloudDataMessageStatusEnum result = cloudDataMessageService
        .sendCloudDataMessage(cloudDataMessage.getDeviceToken(),
            cloudDataMessage.getCloudDataMessageAction(),
            cloudDataMessage.getTopic(),
            cloudDataMessage.getData(), cloudDataMessage.getAttributes(),
            cloudDataMessage.getIso3Language());
    logger().info(loggerPrefix + "Cloud Data Message status {} ", result);
  }
}
