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
import org.jhapy.cqrs.command.notification.CreateSmsCommand;
import org.jhapy.cqrs.command.notification.DeleteSmsCommand;
import org.jhapy.cqrs.command.notification.UpdateSmsCommand;
import org.jhapy.cqrs.event.notification.SmsCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsDeletedEvent;
import org.jhapy.cqrs.event.notification.SmsUpdatedEvent;
import org.jhapy.notification.converter.SmsConverter;
import org.jhapy.notification.domain.SmsStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class SmsAggregate extends AbstractBaseAggregate {
  private String phoneNumber;

  private String body;

  private String smsAction;

  private SmsStatusEnum smsStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private String applicationName;

  private String iso3Language;

  private transient SmsConverter converter;

  @CommandHandler
  public SmsAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") CreateSmsCommand command,
      @Autowired SmsConverter smsConverter) {
    this.converter = smsConverter;

    if (StringUtils.isBlank(command.getEntity().getSmsAction())) {
      throw new IllegalArgumentException("Sms action can not be empty");
    }

    SmsCreatedEvent event = converter.toSmsCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(SmsConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateSmsCommand command) {
    SmsUpdatedEvent event = converter.toSmsUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteSmsCommand command) {
    SmsDeletedEvent event = new SmsDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(SmsCreatedEvent event) {
    converter.updateAggregateFromSmsCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(SmsUpdatedEvent event) {
    converter.updateAggregateFromSmsUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(SmsDeletedEvent event) {
    markDeleted();
  }
}
