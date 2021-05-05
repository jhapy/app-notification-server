package org.jhapy.notification.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.jhapy.commons.config.AppProperties;
import org.jhapy.commons.utils.HasLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer, HasLogger {

  private final Logger log = LoggerFactory.getLogger(WebConfigurer.class);

  private final Environment env;

  private final AppProperties appProperties;

  public WebConfigurer(Environment env, AppProperties appProperties) {
    this.env = env;
    this.appProperties = appProperties;
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    if (env.getActiveProfiles().length != 0) {
      log.info("Web application configuration, using profiles: {}",
          env.getActiveProfiles());
    }
    log.info("Web application fully configured");
  }

  @Bean
  public CorsFilter corsFilter() {
    String loggerPrefix = getLoggerPrefix("corsFilter");
    var source = new UrlBasedCorsConfigurationSource();
    var config = appProperties.getCors();
    if (config.getAllowedOrigins() != null && !config.getAllowedOrigins().isEmpty()) {
      log.debug(loggerPrefix, "Registering CORS filter");
      source.registerCorsConfiguration("/api/**", config);
      source.registerCorsConfiguration("/management/**", config);
      source.registerCorsConfiguration("/v2/api-docs", config);
      source.registerCorsConfiguration("/swagger-ui.html**", config);
    }
    return new CorsFilter(source);
  }

}
