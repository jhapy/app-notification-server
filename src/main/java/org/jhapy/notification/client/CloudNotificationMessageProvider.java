package org.jhapy.notification.client;

import org.springframework.stereotype.Service;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-10
 */
@Service
public interface CloudNotificationMessageProvider {

  String sendCloudNotificationMessage(String deviceToken, String title, String body, String data,
      String id);
}
