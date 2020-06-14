package org.jhapy.notification.config;

import javax.servlet.http.HttpServletRequest;
import org.javers.spring.auditable.AuthorProvider;
import org.jhapy.commons.security.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 20/04/2020
 */
@Configuration
public class JaversBaseSqlConfiguration {

  @Bean
  public AuthorProvider authorProvider() {
    return () -> {
      String currentUsername = "Unknown";
      try {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
          HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes)
              .getRequest();
          currentUsername = servletRequest.getHeader("X-SecUsername");
        } else {
          currentUsername = SecurityUtils.getUsername();
        }
      } catch (IllegalStateException e) {
      }
      return currentUsername;
    };
  }
}
