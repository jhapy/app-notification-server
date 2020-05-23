package org.jhapy.notification.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private final FirebaseConfig firebase = new FirebaseConfig();

  @Data
  public static class FirebaseConfig {

    private String url;
    private String credentialsFile;
    private String projectId;
  }
}
