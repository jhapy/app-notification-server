package org.jhapy.notification.config;

import org.javers.spring.auditable.AuthorProvider;
import org.jhapy.commons.config.Constants;
import org.jhapy.commons.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 20/04/2020
 */
@Configuration
public class JaversBaseSqlConfiguration {

  @Bean
  public AuthorProvider authorProvider() {
    return () -> SecurityUtils.getCurrentUserLogin().orElse(Constants.ANONYMOUS_USER);
  }
}
