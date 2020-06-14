package org.jhapy.notification.service;


import java.util.Map;
import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.domain.CloudDataMessageStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
public interface CloudDataMessageService extends BaseCrudService<CloudDataMessage> {

  Page<CloudDataMessage> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  void sendSimpleCloudDataMessage(String deviceToken, String topic, String body);

  CloudDataMessageStatusEnum sendCloudDataMessage(String deviceToken, String cloudDataMessageAction,
      String topic,
      String data, Map<String, String> attributes, String iso3Language);

  void processNotSentCloudDataMessages();
}
