package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.SmsTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingSmsTemplateQuery;
import org.jhapy.cqrs.query.notification.GetSmsTemplateByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.converter.SmsTemplateConverter;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.repository.SmsTemplateRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("sms-template-group")
public class SmsTemplateEventHandler implements HasLogger {
  private final SmsTemplateRepository repository;
  private final SmsTemplateConverter converter;
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
  public void on(SmsTemplateCreatedEvent event) throws Exception {
    SmsTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetSmsTemplateByIdQuery.class, query -> true, converter.asDTO(entity, null));

    queryUpdateEmitter.emit(
        CountAnyMatchingSmsTemplateQuery.class, query -> true, new CountChangeResult());
  }

  @EventHandler
  public void on(SmsTemplateUpdatedEvent event) throws Exception {
    SmsTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetSmsTemplateByIdQuery.class, query -> true, converter.asDTO(entity, null));
  }

  @EventHandler
  public void on(SmsTemplateDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }
}
