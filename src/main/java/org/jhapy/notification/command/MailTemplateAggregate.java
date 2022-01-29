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
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.command.AbstractBaseAggregate;
import org.jhapy.cqrs.command.notification.CreateMailTemplateCommand;
import org.jhapy.cqrs.command.notification.DeleteMailTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateMailTemplateCommand;
import org.jhapy.cqrs.event.notification.MailTemplateCreatedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateDeletedEvent;
import org.jhapy.cqrs.event.notification.MailTemplateUpdatedEvent;
import org.jhapy.notification.converter.MailTemplateConverter;
import org.springframework.beans.factory.annotation.Autowired;

import static org.axonframework.modelling.command.AggregateLifecycle.markDeleted;

@Aggregate
@NoArgsConstructor
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MailTemplateAggregate extends AbstractBaseAggregate implements HasLogger {
  private String name;

  private String subject;

  private String body;

  private String bodyHtml;

  private String copyTo;

  private String from;

  private String iso3Language;

  private String mailAction;

  private transient MailTemplateConverter converter;

  @CommandHandler
  public MailTemplateAggregate(
      @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
          CreateMailTemplateCommand command,
      @Autowired MailTemplateConverter actionConverter) {
    this.converter = actionConverter;

    if (StringUtils.isBlank(command.getEntity().getName())) {
      throw new IllegalArgumentException("Entity name can not be empty");
    }

    MailTemplateCreatedEvent event = converter.toMailTemplateCreatedEvent(command.getEntity());
    event.setId(command.getId());
    AggregateLifecycle.apply(event);
  }

  @Autowired
  public void setConverter(MailTemplateConverter converter) {
    this.converter = converter;
  }

  @CommandHandler
  public void handle(UpdateMailTemplateCommand command) {
    MailTemplateUpdatedEvent event = converter.toMailTemplateUpdatedEvent(command.getEntity());
    AggregateLifecycle.apply(event);
  }

  @CommandHandler
  public void handle(DeleteMailTemplateCommand command) {
    MailTemplateDeletedEvent event = new MailTemplateDeletedEvent(command.getId());
    AggregateLifecycle.apply(event);
  }

  @EventSourcingHandler
  public void on(MailTemplateCreatedEvent event) {
    String loggerPrefix = getLoggerPrefix("onMailTemplateCreatedEvent");
    debug(loggerPrefix, "In with : " + event.getId() + ", " + event.getMailAction());

    converter.updateAggregateFromMailTemplateCreatedEvent(event, this);

    debug(loggerPrefix, "Out with : " + event.getId() + ", " + event.getMailAction());
  }

  @EventSourcingHandler
  public void on(MailTemplateUpdatedEvent event) {
    converter.updateAggregateFromMailTemplateUpdatedEvent(event, this);
  }

  @EventSourcingHandler
  public void on(MailTemplateDeletedEvent event) {
    markDeleted();
  }
}
