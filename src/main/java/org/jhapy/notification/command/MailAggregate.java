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
import org.jhapy.cqrs.command.notification.CreateMailCommand;
import org.jhapy.cqrs.command.notification.DeleteMailCommand;
import org.jhapy.cqrs.command.notification.UpdateMailCommand;
import org.jhapy.cqrs.event.notification.MailCreatedEvent;
import org.jhapy.cqrs.event.notification.MailDeletedEvent;
import org.jhapy.cqrs.event.notification.MailUpdatedEvent;
import org.jhapy.notification.converter.MailConverter;
import org.jhapy.notification.domain.MailStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MailAggregate extends AbstractBaseAggregate {
  private String mailAction;

  private String to;

  private String copyTo;

  private String from;

  private String subject;

  private String body;

  private Map<String, byte[]> attachments;

  private Map<String, String> attributes;

  private String applicationName;

  private String iso3Language;

  private MailStatusEnum mailStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private transient MailConverter converter;

  @CommandHandler
  public MailAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") CreateMailCommand command,
      @Autowired MailConverter mailConverter) {
    this.converter = mailConverter;

    if (StringUtils.isBlank(command.getEntity().getMailAction())) {
      throw new IllegalArgumentException("Mail action can not be empty");
    }

    MailCreatedEvent event = converter.toMailCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(MailConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateMailCommand command) {
    MailUpdatedEvent event = converter.toMailUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteMailCommand command) {
    MailDeletedEvent event = new MailDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(MailCreatedEvent event) {
    converter.updateAggregateFromMailCreatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(MailUpdatedEvent event) {
    converter.updateAggregateFromMailUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(MailDeletedEvent event) {
    markDeleted();
  }
}
