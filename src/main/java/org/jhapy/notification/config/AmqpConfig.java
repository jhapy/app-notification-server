package org.jhapy.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 27/03/2021
 */
@Configuration
public class AmqpConfig {

  @Bean
  public Queue cloudDataQueue() {
    return new Queue("notification.cloudData", true);
  }

  @Bean
  public Queue cloudNotificationQueue() {
    return new Queue("notification.cloudNotification", true);
  }

  @Bean
  public Queue mailboxQueue() {
    return new Queue("notification.mailbox", true);
  }

  @Bean
  public Queue smsQueue() {
    return new Queue("notification.sms", true);
  }
}

