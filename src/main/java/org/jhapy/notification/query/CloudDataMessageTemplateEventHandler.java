package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingCloudDataMessageTemplateQuery;
import org.jhapy.cqrs.query.notification.GetCloudDataMessageTemplateByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.converter.CloudDataMessageTemplateConverter;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.repository.CloudDataMessageTemplateRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("cloud-data-message-template-group")
public class CloudDataMessageTemplateEventHandler implements HasLogger {
  private final CloudDataMessageTemplateRepository repository;
  private final CloudDataMessageTemplateConverter converter;
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
  public void on(CloudDataMessageTemplateCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onCloudDataMessageTemplateCreatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getCloudDataMessageAction());

    CloudDataMessageTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetCloudDataMessageTemplateByIdQuery.class, query -> true, converter.asDTO(entity, null));

    queryUpdateEmitter.emit(
        CountAnyMatchingCloudDataMessageTemplateQuery.class,
        query -> true,
        new CountChangeResult());

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getCloudDataMessageAction());
  }

  @EventHandler
  public void on(CloudDataMessageTemplateUpdatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onCloudDataMessageTemplateUpdatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getCloudDataMessageAction());

    CloudDataMessageTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetCloudDataMessageTemplateByIdQuery.class, query -> true, converter.asDTO(entity, null));

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getCloudDataMessageAction());
  }

  @EventHandler
  public void on(CloudDataMessageTemplateDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }
}
