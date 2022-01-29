package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.MailTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingMailTemplateQuery;
import org.jhapy.cqrs.query.notification.GetMailTemplateByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.converter.MailTemplateConverter;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.repository.MailTemplateRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("mail-template-group")
public class MailTemplateEventHandler implements HasLogger {
  private final MailTemplateRepository repository;
  private final MailTemplateConverter converter;
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
  public void on(MailTemplateCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onMailTemplateCreatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getMailAction());

    MailTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetMailTemplateByIdQuery.class, query -> true, converter.asDTO(entity, null));

    queryUpdateEmitter.emit(
        CountAnyMatchingMailTemplateQuery.class, query -> true, new CountChangeResult());

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getMailAction());
  }

  @EventHandler
  public void on(MailTemplateUpdatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onMailTemplateUpdatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getMailAction());

    MailTemplate entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetMailTemplateByIdQuery.class, query -> true, converter.asDTO(entity, null));

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getMailAction());
  }

  @EventHandler
  public void on(MailTemplateDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }
}
