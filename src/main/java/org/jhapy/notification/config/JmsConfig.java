package org.jhapy.notification.config;

import javax.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-02
 */
@Configuration
@EnableJms
public class JmsConfig {

  @Autowired
  ConnectionFactory connectionFactory;

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setConcurrency("1-1");
    return factory;
  }
}
