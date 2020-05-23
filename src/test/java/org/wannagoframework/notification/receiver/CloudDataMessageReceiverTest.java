package org.jhapy.notification.receiver;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.jhapy.dto.domain.notification.CloudDataMessage;
import org.jhapy.dto.domain.notification.CloudNotificationMessage;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 13/04/2020
 */
@SpringBootTest
@ActiveProfiles({"local", "no-eureka"})
class CloudDataMessageReceiverTest {

  @Autowired
  private CloudDataMessageReceiver cloudDataMessageReceiver;

  @Autowired
  private CloudNotificationMessageReceiver cloudNotificationMessageReceiver;

  @Test
  void onNewCloudDataMessage() {
    CloudDataMessage testMessage = new CloudDataMessage();
    testMessage.setDeviceToken(
        "f6D368imRsmqLT3y192P7r:APA91bGbqMJ8HJ8HwZwYsNXNlQlmJHa3EXvzlsSFA17ynp4uvJiKefY-qLWhNSu4jib-xykQ8feXypJEm16qepAerLnOG5edro_e0EX3N3HT8lRf-TTxA5uypBdW7Qd3IqW76TgtBHGR");
    testMessage.setData("{\"Nick\" : \"Mario\",\n"
        + "      \"body\" : \"great match!\"}");
    testMessage.setApplicationName("Test");

    cloudDataMessageReceiver.onNewCloudDataMessage(testMessage);
  }

  @Test
  void onNewCloudDataWithTopicMessage() {
    CloudDataMessage testMessage = new CloudDataMessage();
    testMessage.setData("{\"Nick\" : \"Mario\",\n"
        + "      \"body\" : \"great match!\"}");
    testMessage.setTopic("Test Topic");
    testMessage.setApplicationName("Test");

    cloudDataMessageReceiver.onNewCloudDataMessage(testMessage);
  }

  @Test
  void onNewCloudNotificationMessage() {
    CloudNotificationMessage testMessage = new CloudNotificationMessage();
    testMessage.setDeviceToken(
        "f6D368imRsmqLT3y192P7r:APA91bGbqMJ8HJ8HwZwYsNXNlQlmJHa3EXvzlsSFA17ynp4uvJiKefY-qLWhNSu4jib-xykQ8feXypJEm16qepAerLnOG5edro_e0EX3N3HT8lRf-TTxA5uypBdW7Qd3IqW76TgtBHGR");
    testMessage.setTitle("my Title");
    testMessage.setBody("My Body");
    testMessage.setApplicationName("Test");
    cloudNotificationMessageReceiver.onNewCloudNotificationMessage(testMessage);
  }
}