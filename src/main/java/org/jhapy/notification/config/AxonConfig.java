package org.jhapy.notification.config;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import com.thoughtworks.xstream.security.WildcardTypePermission;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.axonframework.messaging.Message;
import org.axonframework.messaging.interceptors.LoggingInterceptor;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.jhapy.cqrs.query.AbstractBaseQuery;
import org.jhapy.dto.domain.BaseEntity;
import org.jhapy.dto.serviceQuery.BaseRemoteQuery;
import org.jhapy.notification.command.interceptor.CreateOrUpdateCloudDataMessageTemplateCommandInterceptor;
import org.jhapy.notification.command.interceptor.CreateOrUpdateCloudNotificationMessageTemplateCommandInterceptor;
import org.jhapy.notification.command.interceptor.CreateOrUpdateMailTemplateCommandInterceptor;
import org.jhapy.notification.command.interceptor.CreateOrUpdateSmsTemplateCommandInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AxonConfig {

  @Bean
  public LoggingInterceptor<Message<?>> loggingInterceptor() {
    return new LoggingInterceptor<>();
  }

  @Autowired
  public void configureLoggingInterceptorFor(
      CommandBus commandBus, LoggingInterceptor<Message<?>> loggingInterceptor) {
    commandBus.registerDispatchInterceptor(loggingInterceptor);
    commandBus.registerHandlerInterceptor(loggingInterceptor);
  }

  @Autowired
  public void configureLoggingInterceptorFor(
      EventBus eventBus, LoggingInterceptor<Message<?>> loggingInterceptor) {
    eventBus.registerDispatchInterceptor(loggingInterceptor);
  }

  @Autowired
  public void configureLoggingInterceptorFor(
      EventProcessingConfigurer eventProcessingConfigurer,
      LoggingInterceptor<Message<?>> loggingInterceptor) {
    eventProcessingConfigurer.registerDefaultHandlerInterceptor(
        (config, processorName) -> loggingInterceptor);
  }

  @Autowired
  public void configureLoggingInterceptorFor(
      QueryBus queryBus, LoggingInterceptor<Message<?>> loggingInterceptor) {
    queryBus.registerDispatchInterceptor(loggingInterceptor);
    queryBus.registerHandlerInterceptor(loggingInterceptor);
  }

  @Autowired
  public void configureErrorHandlers(EventProcessingConfigurer configurer) {
    configurer.registerListenerInvocationErrorHandler(
        "mail-group", configuration -> PropagatingErrorHandler.instance());
    configurer.registerListenerInvocationErrorHandler(
        "mail-template-group", configuration -> PropagatingErrorHandler.instance());
    configurer.registerListenerInvocationErrorHandler(
        "sms-group", configuration -> PropagatingErrorHandler.instance());
    configurer.registerListenerInvocationErrorHandler(
        "sms-template-group", configuration -> PropagatingErrorHandler.instance());
    configurer.registerListenerInvocationErrorHandler(
        "cloud-data-message-group", configuration -> PropagatingErrorHandler.instance());
    configurer.registerListenerInvocationErrorHandler(
        "cloud-notification-message-group", configuration -> PropagatingErrorHandler.instance());
  }

  @Autowired
  public void registerCreateOrUpdateMailTemplateCommandInterceptor(
      CreateOrUpdateMailTemplateCommandInterceptor createOrUpdateMailTemplateCommandInterceptor,
      CommandBus commandBus) {
    commandBus.registerDispatchInterceptor(createOrUpdateMailTemplateCommandInterceptor);
  }

  @Autowired
  public void registerCreateOrUpdateSmsTemplateCommandInterceptor(
      CreateOrUpdateSmsTemplateCommandInterceptor createOrUpdateSmsTemplateCommandInterceptor,
      CommandBus commandBus) {
    commandBus.registerDispatchInterceptor(createOrUpdateSmsTemplateCommandInterceptor);
  }

  @Autowired
  public void registerCreateOrUpdateCloudDataMessageTemplateCommandInterceptor(
      CreateOrUpdateCloudDataMessageTemplateCommandInterceptor
          createOrUpdateCloudDataMessageTemplateCommandInterceptor,
      CommandBus commandBus) {
    commandBus.registerDispatchInterceptor(
        createOrUpdateCloudDataMessageTemplateCommandInterceptor);
  }

  @Autowired
  public void registerCreateOrUpdateCloudNotificationMessageTemplateCommandInterceptor(
      CreateOrUpdateCloudNotificationMessageTemplateCommandInterceptor
          createOrUpdateCloudNotificationMessageTemplateCommandInterceptor,
      CommandBus commandBus) {
    commandBus.registerDispatchInterceptor(
        createOrUpdateCloudNotificationMessageTemplateCommandInterceptor);
  }

  @Bean
  @Qualifier("messageSerializer")
  public Serializer messageSerializer() {
    XStream xStream = new XStream();
    xStream.addPermission(new TypeHierarchyPermission(BaseEntity.class));
    xStream.addPermission(new TypeHierarchyPermission(BaseRemoteQuery.class));
    xStream.addPermission(new TypeHierarchyPermission(AbstractBaseQuery.class));
    xStream.addPermission(
        new WildcardTypePermission(new String[] {"org.jhapy.dto.**", "org.jhapy.cqrs.**"}));

    return XStreamSerializer.builder().xStream(xStream).build();
  }
}
