package org.jhapy.notification.client;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.config.AppProperties;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-10
 */
@Service
public class FirebaseCloudMessagingProvider
    implements CloudNotificationMessageProvider, CloudDataMessageProvider, HasLogger {

  private static final String MESSAGING_SCOPE =
      "https://www.googleapis.com/auth/firebase.messaging";
  private static final String[] SCOPES = {MESSAGING_SCOPE};

  private final AppProperties appProperties;

  public FirebaseCloudMessagingProvider(AppProperties appProperties) {
    var loggerPrefix = getLoggerPrefix("FirebaseCloudMessagingProvider");
    this.appProperties = appProperties;
    FirebaseOptions options = null;

    if (StringUtils.isNotBlank(appProperties.getFirebase().getCredentialsFile())
        && StringUtils.isNotBlank(appProperties.getFirebase().getUrl())) {
      try {
        options =
            new FirebaseOptions.Builder()
                .setCredentials(
                    GoogleCredentials.fromStream(
                        new FileInputStream(appProperties.getFirebase().getCredentialsFile())))
                .setDatabaseUrl(appProperties.getFirebase().getUrl())
                .build();
      } catch (IOException e) {
        e.printStackTrace();
      }

      FirebaseApp.initializeApp(options);
    } else {
      error(loggerPrefix, "Firebase Config is not set, skip");
    }
  }

  private AccessToken getAccessToken() throws IOException {
    GoogleCredentials googleCredential =
        GoogleCredentials.fromStream(
                new FileInputStream(appProperties.getFirebase().getCredentialsFile()))
            .createScoped(Arrays.asList(SCOPES));
    // googleCredential.refreshAccessToken();
    return googleCredential.getAccessToken();
  }

  public String sendCloudDataMessage(String deviceToken, String topic, String body, UUID id) {
    var jsonElement = (JsonObject) JsonParser.parseString(body);
    Map<String, String> values = new HashMap<>();

    jsonElement
        .keySet()
        .forEach(s -> values.put(s, jsonElement.getAsJsonPrimitive(s).getAsString()));
    var builder = Message.builder().putAllData(values);
    if (StringUtils.isNotBlank(deviceToken)) {
      builder.setToken(deviceToken);
    } else if (StringUtils.isNotBlank(topic)) {
      builder.setTopic(topic);
    }

    var message = builder.build();
    try {
      return FirebaseMessaging.getInstance().send(message);
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public String sendCloudNotificationMessage(
      String deviceToken, String title, String body, String data, UUID id) {
    var builder =
        Message.builder()
            .setNotification(Notification.builder().setTitle(title).setBody(body).build());
    if (StringUtils.isNotBlank(data)) {
      var jsonElement = (JsonObject) JsonParser.parseString(body);
      Map<String, String> values = new HashMap<>();

      jsonElement
          .keySet()
          .forEach(s -> values.put(s, jsonElement.getAsJsonPrimitive(s).getAsString()));

      builder.putAllData(values);
    }
    var message = builder.setToken(deviceToken).build();

    try {
      return FirebaseMessaging.getInstance().send(message);
    } catch (FirebaseMessagingException e) {
      e.printStackTrace();
    }
    return null;
  }
}