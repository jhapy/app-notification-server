package org.jhapy.notification.command;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.jhapy.cqrs.command.AbstractBaseAggregate;
import org.jhapy.cqrs.command.notification.CreateCloudNotificationMessageTemplateCommand;
import org.jhapy.cqrs.command.notification.DeleteCloudNotificationMessageTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateCloudNotificationMessageTemplateCommand;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageTemplateUpdatedEvent;
import org.jhapy.notification.converter.CloudNotificationMessageTemplateConverter;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CloudNotificationMessageTemplateAggregate extends AbstractBaseAggregate {
  private String name;

  private String title;

  private String body;

  private String data;

  private String iso3Language;

  private String cloudNotificationMessageAction;

  private transient CloudNotificationMessageTemplateConverter converter;

  @CommandHandler
  public CloudNotificationMessageTemplateAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          CreateCloudNotificationMessageTemplateCommand command,
      @Autowired CloudNotificationMessageTemplateConverter actionConverter) {
    this.converter = actionConverter;

    if (StringUtils.isBlank(command.getEntity().getName())) {
      throw new IllegalArgumentException("Entity name can not be empty");
    }

    CloudNotificationMessageTemplateCreatedEvent event =
        converter.toCloudNotificationMessageTemplateCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(CloudNotificationMessageTemplateConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateCloudNotificationMessageTemplateCommand command) {
    CloudNotificationMessageTemplateUpdatedEvent event =
        converter.toCloudNotificationMessageTemplateUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteCloudNotificationMessageTemplateCommand command) {
    CloudNotificationMessageTemplateDeletedEvent event =
        new CloudNotificationMessageTemplateDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(CloudNotificationMessageTemplateCreatedEvent event) {
    converter.updateAggregateFromCloudNotificationMessageTemplateCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudNotificationMessageTemplateUpdatedEvent event) {
    converter.updateAggregateFromCloudNotificationMessageTemplateUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudNotificationMessageTemplateDeletedEvent event) {
    markDeleted();
  }
}
