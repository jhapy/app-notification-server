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
import org.jhapy.cqrs.command.notification.CreateCloudDataMessageCommand;
import org.jhapy.cqrs.command.notification.DeleteCloudDataMessageCommand;
import org.jhapy.cqrs.command.notification.UpdateCloudDataMessageCommand;
import org.jhapy.cqrs.event.notification.CloudDataMessageCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageUpdatedEvent;
import org.jhapy.notification.converter.CloudDataMessageConverter;
import org.jhapy.notification.domain.CloudDataMessageStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CloudDataMessageAggregate extends AbstractBaseAggregate {
  private String deviceToken;

  private String data;

  private String topic;

  private String cloudDataMessageAction;

  private CloudDataMessageStatusEnum cloudDataMessageStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private String applicationName;

  private String iso3Language;

  private transient CloudDataMessageConverter converter;

  @CommandHandler
  public CloudDataMessageAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          CreateCloudDataMessageCommand command,
      @Autowired CloudDataMessageConverter mailConverter) {
    this.converter = mailConverter;

    if (StringUtils.isBlank(command.getEntity().getCloudDataMessageAction())) {
      throw new IllegalArgumentException("CloudDataMessage action can not be empty");
    }

    CloudDataMessageCreatedEvent event =
        converter.toCloudDataMessageCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(CloudDataMessageConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateCloudDataMessageCommand command) {
    CloudDataMessageUpdatedEvent event =
        converter.toCloudDataMessageUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteCloudDataMessageCommand command) {
    CloudDataMessageDeletedEvent event = new CloudDataMessageDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(CloudDataMessageCreatedEvent event) {
    converter.updateAggregateFromCloudDataMessageCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudDataMessageUpdatedEvent event) {
    converter.updateAggregateFromCloudDataMessageUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudDataMessageDeletedEvent event) {
    markDeleted();
  }
}
