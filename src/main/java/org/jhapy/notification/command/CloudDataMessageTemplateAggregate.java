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
import org.jhapy.cqrs.command.notification.CreateCloudDataMessageTemplateCommand;
import org.jhapy.cqrs.command.notification.DeleteCloudDataMessageTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateCloudDataMessageTemplateCommand;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageTemplateUpdatedEvent;
import org.jhapy.notification.converter.CloudDataMessageTemplateConverter;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class CloudDataMessageTemplateAggregate extends AbstractBaseAggregate {
  private String name;

  private String data;

  private String iso3Language;

  private String cloudDataMessageAction;

  private transient CloudDataMessageTemplateConverter converter;

  @CommandHandler
  public CloudDataMessageTemplateAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          CreateCloudDataMessageTemplateCommand command,
      @Autowired CloudDataMessageTemplateConverter actionConverter) {
    this.converter = actionConverter;

    if (StringUtils.isBlank(command.getEntity().getName())) {
      throw new IllegalArgumentException("Entity name can not be empty");
    }

    CloudDataMessageTemplateCreatedEvent event =
        converter.toCloudDataMessageTemplateCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(CloudDataMessageTemplateConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateCloudDataMessageTemplateCommand command) {
    CloudDataMessageTemplateUpdatedEvent event =
        converter.toCloudDataMessageTemplateUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteCloudDataMessageTemplateCommand command) {
    CloudDataMessageTemplateDeletedEvent event =
        new CloudDataMessageTemplateDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(CloudDataMessageTemplateCreatedEvent event) {
    converter.updateAggregateFromCloudDataMessageTemplateCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudDataMessageTemplateUpdatedEvent event) {
    converter.updateAggregateFromCloudDataMessageTemplateUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(CloudDataMessageTemplateDeletedEvent event) {
    markDeleted();
  }
}
