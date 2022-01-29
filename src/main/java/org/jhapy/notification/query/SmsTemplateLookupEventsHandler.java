package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.jhapy.cqrs.event.notification.SmsTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateUpdatedEvent;
import org.jhapy.notification.domain.SmsTemplateLookup;
import org.jhapy.notification.repository.SmsTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("sms-template-group")
@RequiredArgsConstructor
public class SmsTemplateLookupEventsHandler {

  private final SmsTemplateLookupRepository repository;

  @EventHandler
  public void on(SmsTemplateCreatedEvent event) {
    SmsTemplateLookup entity = new SmsTemplateLookup(event.getId(), event.getSmsAction());
    repository.save(entity);
  }

  @EventHandler
  public void on(SmsTemplateUpdatedEvent event) {
    Optional<SmsTemplateLookup> optEntity = repository.findById(event.getId());
    if (optEntity.isPresent()) {
      SmsTemplateLookup entity = optEntity.get();
      entity.setSmsAction(event.getSmsAction());
      repository.save(entity);
    }
  }

  @EventHandler
  public void on(SmsTemplateDeletedEvent event) {
    repository.deleteById(event.getId());
  }

  @ResetHandler
  public void reset() {
    repository.deleteAll();
  }
}
