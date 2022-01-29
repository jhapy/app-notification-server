package org.jhapy.notification.service;

import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
public interface CloudNotificationMessageService extends BaseCrudService<CloudNotificationMessage> {

  Page<CloudNotificationMessage> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  void sendSimpleCloudNotificationMessage(
      String deviceToken, String title, String body, String data);

  CloudNotificationMessageStatusEnum sendCloudNotificationMessage(
      String deviceToken,
      String cloudNotificationMessageAction,
      String title,
      String body,
      String data,
      Map<String, String> attributes,
      String iso3Language);

  void processNotSentCloudNotificationMessages();
}
