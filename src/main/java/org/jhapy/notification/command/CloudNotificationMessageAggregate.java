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
import org.jhapy.cqrs.command.notification.CreateCloudNotificationMessageCommand;
import org.jhapy.cqrs.command.notification.DeleteCloudNotificationMessageCommand;
import org.jhapy.cqrs.command.notification.UpdateCloudNotificationMessageCommand;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageUpdatedEvent;
import org.jhapy.notification.converter.CloudNotificationMessageConverter;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CloudNotificationMessageAggregate extends AbstractBaseAggregate {
  private String deviceToken;

  private String title;

  private String body;

  private String data;

  private String cloudNotificationMessageAction;

  private CloudNotificationMessageStatusEnum cloudNotificationMessageStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private String applicationName;

  private String iso3Language;

  private transient CloudNotificationMessageConverter converter;

  @CommandHandler
  public CloudNotificationMessageAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          CreateCloudNotificationMessageCommand command,
      @Autowired CloudNotificationMessageConverter mailConverter) {
    this.converter = mailConverter;

    if (StringUtils.isBlank(command.getEntity().getCloudNotificationMessageAction())) {
      throw new IllegalArgumentException("CloudNotificationMessage action can not be empty");
    }

    CloudNotificationMessageCreatedEvent event =
        converter.toCloudNotificationMessageCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(CloudNotificationMessageConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateCloudNotificationMessageCommand command) {
    CloudNotificationMessageUpdatedEvent event =
        converter.toCloudNotificationMessageUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteCloudNotificationMessageCommand command) {
    CloudNotificationMessageDeletedEvent event =
        new CloudNotificationMessageDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(CloudNotificationMessageCreatedEvent event) {
    converter.updateAggregateFromCloudNotificationMessageCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudNotificationMessageUpdatedEvent event) {
    converter.updateAggregateFromCloudNotificationMessageUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudNotificationMessageDeletedEvent event) {
    markDeleted();
  }
}
