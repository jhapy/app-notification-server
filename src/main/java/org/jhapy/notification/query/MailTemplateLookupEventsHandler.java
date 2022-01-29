package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.MailTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateUpdatedEvent;
import org.jhapy.notification.domain.MailTemplateLookup;
import org.jhapy.notification.repository.MailTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("mail-template-group")
@RequiredArgsConstructor
public class MailTemplateLookupEventsHandler implements HasLogger {

  private final MailTemplateLookupRepository repository;

  @EventHandler
  public void on(MailTemplateCreatedEvent event) {
    String loggerPrefix = getLoggerPrefix("onMailTemplateCreatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getMailAction());

    MailTemplateLookup entity = new MailTemplateLookup(event.getId(), event.getMailAction());
    repository.save(entity);

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getMailAction());
  }

  @EventHandler
  public void on(MailTemplateUpdatedEvent event) {
    Optional<MailTemplateLookup> optEntity = repository.findById(event.getId());
    if (optEntity.isPresent()) {
      MailTemplateLookup entity = optEntity.get();
      entity.setMailAction(event.getMailAction());
      repository.save(entity);
    }
  }

  @EventHandler
  public void on(MailTemplateDeletedEvent event) {
    repository.deleteById(event.getId());
  }

  @ResetHandler
  public void reset() {
    repository.deleteAll();
  }
}
