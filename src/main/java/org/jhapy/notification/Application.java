package org.jhapy.notification;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.DefaultProfileUtil;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.commons.utils.HasLoggerStatic;
import org.jhapy.commons.utils.SpringProfileConstants;
import org.jhapy.notification.config.AppProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties(AppProperties.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan({"org.jhapy.notification", "org.jhapy.commons"})
public class Application implements InitializingBean, HasLogger {

  private final Environment env;
  private final AppProperties appProperties;

  public Application(Environment env, AppProperties appProperties) {
    this.env = env;
    this.appProperties = appProperties;
  }

  public static void main(String[] args) {
    var app = new SpringApplication(Application.class);
    DefaultProfileUtil.addDefaultProfile(app);
    var env = app.run(args).getEnvironment();
    logApplicationStartup(env);
  }

  private static void logApplicationStartup(Environment env) {
    String loggerPrefix = HasLoggerStatic.getLoggerPrefix("logApplicationStartup");
    var protocol = "http";
    if (env.getProperty("server.ssl.key-store") != null) {
      protocol = "https";
    }
    var serverPort = env.getProperty("server.port");
    var contextPath = env.getProperty("server.servlet.context-path");
    if (StringUtils.isBlank(contextPath)) {
      contextPath = "/";
    }
    var hostAddress = "localhost";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      HasLoggerStatic.warn(Application.class, loggerPrefix,
          "The host name could not be determined, using `localhost` as fallback");
    }
    HasLoggerStatic.info(Application.class, loggerPrefix, """

            ----------------------------------------------------------
            \tApplication '{0}' is running! Access URLs:
            \tLocal: \t\t{1}://localhost:{2}{3}
            \tExternal: \t{4}://{5}:{6}{7}
            \tProfile(s): \t{8}
            ----------------------------------------------------------""",
        env.getProperty("spring.application.name"),
        protocol,
        serverPort,
        contextPath,
        protocol,
        hostAddress,
        serverPort,
        contextPath,
        env.getActiveProfiles());

    var configServerStatus = env.getProperty("configserver.status");
    if (configServerStatus == null) {
      configServerStatus = "Not found or not setup for this application";
    }
    HasLoggerStatic.info(Application.class, loggerPrefix, """

            ----------------------------------------------------------
            \tConfig Server: \t{0}
            ----------------------------------------------------------""",
        configServerStatus);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    String loggerPrefix = getLoggerPrefix("afterPropertiesSet");
    Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
    if (activeProfiles.contains(SpringProfileConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles
        .contains(SpringProfileConstants.SPRING_PROFILE_PRODUCTION)) {
      error(loggerPrefix, "You have misconfigured your application! It should not run " +
          "with both the 'dev' and 'prod' profiles at the same time.");
    }
  }

  @PostConstruct
  void postConstruct() {
    String loggerPrefix = getLoggerPrefix("postConstruct");
    if (StringUtils.isNotBlank(appProperties.getSecurity().getTrustStore().getTrustStorePath())) {
      var trustStoreFilePath = new File(
          appProperties.getSecurity().getTrustStore().getTrustStorePath());
      var tsp = trustStoreFilePath.getAbsolutePath();
      info(loggerPrefix, "Use trustStore {0}, with password : {1}, with type : {2}", tsp,
          appProperties.getSecurity()
              .getTrustStore().getTrustStorePassword(), appProperties.getSecurity()
              .getTrustStore()
              .getTrustStoreType());

      System.setProperty("javax.net.ssl.trustStore", tsp);
      System.setProperty("javax.net.ssl.trustStorePassword",
          appProperties.getSecurity().getTrustStore().getTrustStorePassword());
      if (StringUtils.isNotBlank(appProperties.getSecurity().getTrustStore().getTrustStoreType())) {
        System.setProperty("javax.net.ssl.trustStoreType",
            appProperties.getSecurity().getTrustStore().getTrustStoreType());
      }
    }
    if (StringUtils.isNotBlank(appProperties.getSecurity().getKeyStore().getKeyStorePath())) {
      var keyStoreFilePath = new File(appProperties.getSecurity().getKeyStore().getKeyStorePath());
      var ksp = keyStoreFilePath.getAbsolutePath();
      info(loggerPrefix, "Use keyStore {0}, with password : {1}, with type : {2}", ksp,
          appProperties.getSecurity()
              .getKeyStore().getKeyStorePassword(), appProperties.getSecurity()
              .getKeyStore()
              .getKeyStoreType());

      System.setProperty("javax.net.ssl.keyStore", ksp);
      System.setProperty("javax.net.ssl.keyStorePassword",
          appProperties.getSecurity().getKeyStore().getKeyStorePassword());
      if (StringUtils.isNotBlank(appProperties.getSecurity().getKeyStore().getKeyStoreType())) {
        System.setProperty("javax.net.ssl.keyStoreType",
            appProperties.getSecurity().getKeyStore().getKeyStoreType());
      }
    }
    if (appProperties.getSecurity().getTrustStore().getDebug() != null
        || appProperties.getSecurity().getKeyStore().getDebug() != null) {
      System.setProperty("javax.net.debug",
          Boolean.toString(appProperties.getSecurity().getTrustStore().getDebug() != null
              || appProperties.getSecurity().getKeyStore().getDebug() != null));
    }
  }
}
