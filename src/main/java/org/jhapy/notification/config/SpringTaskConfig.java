package org.jhapy.notification.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Configuration
@EnableScheduling
@ComponentScan({"org.jhapy.notification.task"})
public class SpringTaskConfig {

}
