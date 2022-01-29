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
import org.jhapy.cqrs.command.notification.CreateSmsTemplateCommand;
import org.jhapy.cqrs.command.notification.DeleteSmsTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateSmsTemplateCommand;
import org.jhapy.cqrs.event.notification.SmsTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.SmsTemplateUpdatedEvent;
import org.jhapy.notification.converter.SmsTemplateConverter;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SmsTemplateAggregate extends AbstractBaseAggregate {
  private String name;

  private String body;

  private String iso3Language;

  private String smsAction;

  private transient SmsTemplateConverter converter;

  @CommandHandler
  public SmsTemplateAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          CreateSmsTemplateCommand command,
      @Autowired SmsTemplateConverter actionConverter) {
    this.converter = actionConverter;

    if (StringUtils.isBlank(command.getEntity().getName())) {
      throw new IllegalArgumentException("Entity name can not be empty");
    }

    SmsTemplateCreatedEvent event = converter.toSmsTemplateCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(SmsTemplateConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateSmsTemplateCommand command) {
    SmsTemplateUpdatedEvent event = converter.toSmsTemplateUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteSmsTemplateCommand command) {
    SmsTemplateDeletedEvent event = new SmsTemplateDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(SmsTemplateCreatedEvent event) {
    converter.updateAggregateFromSmsTemplateCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(SmsTemplateUpdatedEvent event) {
    converter.updateAggregateFromSmsTemplateUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(SmsTemplateDeletedEvent event) {
    markDeleted();
  }
}
