package org.jhapy.notification;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.jhapy.commons.utils.DefaultProfileUtil;
import org.jhapy.commons.utils.SpringProfileConstants;
import org.jhapy.notification.config.AppProperties;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableConfigurationProperties(AppProperties.class)
@ComponentScan({"org.jhapy.notification", "org.jhapy.commons"})
public class Application implements InitializingBean {

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  private final Environment env;

  public Application(Environment env) {
    this.env = env;
  }

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(Application.class);
    DefaultProfileUtil.addDefaultProfile(app);
    Environment env = app.run(args).getEnvironment();
    logApplicationStartup(env);
  }

  private static void logApplicationStartup(Environment env) {
    String protocol = "http";
    if (env.getProperty("server.ssl.key-store") != null) {
      protocol = "https";
    }
    String serverPort = env.getProperty("server.port");
    String contextPath = env.getProperty("server.servlet.context-path");
    if (StringUtils.isBlank(contextPath)) {
      contextPath = "/";
    }
    String hostAddress = "localhost";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      logger.warn("The host name could not be determined, using `localhost` as fallback");
    }
    logger.info("\n----------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t" +
            "Local: \t\t{}://localhost:{}{}\n\t" +
            "External: \t{}://{}:{}{}\n\t" +
            "Profile(s): \t{}\n----------------------------------------------------------",
        env.getProperty("spring.application.name"),
        protocol,
        serverPort,
        contextPath,
        protocol,
        hostAddress,
        serverPort,
        contextPath,
        env.getActiveProfiles());

    String configServerStatus = env.getProperty("configserver.status");
    if (configServerStatus == null) {
      configServerStatus = "Not found or not setup for this application";
    }
    logger.info("\n----------------------------------------------------------\n\t" +
            "Config Server: \t{}\n----------------------------------------------------------",
        configServerStatus);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
    if (activeProfiles.contains(SpringProfileConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles
        .contains(SpringProfileConstants.SPRING_PROFILE_PRODUCTION)) {
      logger.error("You have misconfigured your application! It should not run " +
          "with both the 'dev' and 'prod' profiles at the same time.");
    }
  }
}