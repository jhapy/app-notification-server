package org.jhapy.notification.query;

import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ResetHandler;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateUpdatedEvent;
import org.jhapy.notification.domain.CloudNotificationMessageTemplateLookup;
import org.jhapy.notification.repository.CloudNotificationMessageTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@ProcessingGroup("cloud-notification-message-template-group")
@RequiredArgsConstructor
public class CloudNotificationMessageTemplateLookupEventsHandler implements HasLogger {

  private final CloudNotificationMessageTemplateLookupRepository repository;

  @EventHandler
  public void on(CloudNotificationMessageTemplateCreatedEvent event) {
    String loggerPrefix = getLoggerPrefix("onCloudNotificationMessageTemplateCreatedEvent");
    debug(
        loggerPrefix,
        "In with : " + event.getId() + ", " + event.getCloudNotificationMessageAction());

    CloudNotificationMessageTemplateLookup entity =
        new CloudNotificationMessageTemplateLookup(
            event.getId(), event.getCloudNotificationMessageAction());
    repository.save(entity);

    debug(
        loggerPrefix,
        "Out with : " + event.getId() + ", " + event.getCloudNotificationMessageAction());
  }

  @EventHandler
  public void on(CloudNotificationMessageTemplateUpdatedEvent event) {
    Optional<CloudNotificationMessageTemplateLookup> optEntity = repository.findById(event.getId());
    if (optEntity.isPresent()) {
      CloudNotificationMessageTemplateLookup entity = optEntity.get();
      entity.setCloudNotificationMessageAction(event.getCloudNotificationMessageAction());
      repository.save(entity);
    }
  }

  @EventHandler
  public void on(CloudNotificationMessageTemplateDeletedEvent event) {
    repository.deleteById(event.getId());
  }

  @ResetHandler
  public void reset() {
    repository.deleteAll();
  }
}
