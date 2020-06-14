package org.jhapy.notification.config.metric;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import org.jhapy.notification.endpoint.JHapyMetricsEndpoint;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>JHapyMetricsEndpointConfiguration class.</p>
 */
@Configuration
@ConditionalOnClass(Timed.class)
@AutoConfigureAfter(MetricsEndpointAutoConfiguration.class)
public class JHapyMetricsEndpointConfiguration {

  /**
   * <p>jHapyMetricsEndpoint.</p>
   *
   * @param meterRegistry a {@link MeterRegistry} object.
   * @return a {@link JHapyMetricsEndpoint} object.
   */
  @Bean
  @ConditionalOnMissingBean
  @ConditionalOnAvailableEndpoint
  public JHapyMetricsEndpoint jHapyMetricsEndpoint(MeterRegistry meterRegistry) {
    return new JHapyMetricsEndpoint(meterRegistry);
  }
}