package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateUpdatedEvent;
import org.jhapy.notification.domain.CloudDataMessageTemplateLookup;
import org.jhapy.notification.repository.CloudDataMessageTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("cloud-data-message-template-group")
@RequiredArgsConstructor
public class CloudDataMessageTemplateLookupEventsHandler implements HasLogger {

  private final CloudDataMessageTemplateLookupRepository repository;

  @EventHandler
  public void on(CloudDataMessageTemplateCreatedEvent event) {
    String loggerPrefix = getLoggerPrefix("onCloudDataMessageTemplateCreatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getCloudDataMessageAction());

    CloudDataMessageTemplateLookup entity =
        new CloudDataMessageTemplateLookup(event.getId(), event.getCloudDataMessageAction());
    repository.save(entity);

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getCloudDataMessageAction());
  }

  @EventHandler
  public void on(CloudDataMessageTemplateUpdatedEvent event) {
    Optional<CloudDataMessageTemplateLookup> optEntity = repository.findById(event.getId());
    if (optEntity.isPresent()) {
      CloudDataMessageTemplateLookup entity = optEntity.get();
      entity.setCloudDataMessageAction(event.getCloudDataMessageAction());
      repository.save(entity);
    }
  }

  @EventHandler
  public void on(CloudDataMessageTemplateDeletedEvent event) {
    repository.deleteById(event.getId());
  }

  @ResetHandler
  public void reset() {
    repository.deleteAll();
  }
}
