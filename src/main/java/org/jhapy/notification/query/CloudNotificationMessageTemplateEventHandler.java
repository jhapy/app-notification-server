package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingCloudNotificationMessageTemplateQuery;
import org.jhapy.cqrs.query.notification.GetCloudNotificationMessageTemplateByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.converter.CloudNotificationMessageTemplateConverter;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.repository.CloudNotificationMessageTemplateRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("cloud-notification-message-template-group")
public class CloudNotificationMessageTemplateEventHandler implements HasLogger {
  private final CloudNotificationMessageTemplateRepository repository;
  private final CloudNotificationMessageTemplateConverter converter;
  private final QueryUpdateEmitter queryUpdateEmitter;

  @ExceptionHandler
  public void handleException(Exception ex) throws Exception {
    String loggerPrefix = getLoggerPrefix("handleException");
    error(
        loggerPrefix,
        ex,
        "Exception in EventHandler (ExceptionHandler): {0}:{1}",
        ex.getClass().getName(),
        ex.getMessage());
    throw ex;
  }

  @EventHandler
  public void on(CloudNotificationMessageTemplateCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onCloudNotificationMessageTemplateCreatedEvent");
    debug(
        loggerPrefix,
        "In with : " + event.getId() + ", " + event.getCloudNotificationMessageAction());

    CloudNotificationMessageTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetCloudNotificationMessageTemplateByIdQuery.class,
        query -> true,
        converter.asDTO(entity, null));

    queryUpdateEmitter.emit(
        CountAnyMatchingCloudNotificationMessageTemplateQuery.class,
        query -> true,
        new CountChangeResult());

    debug(
        loggerPrefix,
        "Out with : " + event.getId() + ", " + event.getCloudNotificationMessageAction());
  }

  @EventHandler
  public void on(CloudNotificationMessageTemplateUpdatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onCloudNotificationMessageTemplateUpdatedEvent");
    debug(
        loggerPrefix,
        "In with : " + event.getId() + ", " + event.getCloudNotificationMessageAction());

    CloudNotificationMessageTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetCloudNotificationMessageTemplateByIdQuery.class,
        query -> true,
        converter.asDTO(entity, null));

    debug(
        loggerPrefix,
        "Out with : " + event.getId() + ", " + event.getCloudNotificationMessageAction());
  }

  @EventHandler
  public void on(CloudNotificationMessageTemplateDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }
}
