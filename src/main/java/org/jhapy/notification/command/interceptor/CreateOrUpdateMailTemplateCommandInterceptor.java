package org.jhapy.notification.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.command.notification.CreateMailTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateMailTemplateCommand;
import org.jhapy.notification.repository.MailTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CreateOrUpdateMailTemplateCommandInterceptor
    implements MessageDispatchInterceptor<CommandMessage<?>>, HasLogger {
  static final String MAIL_TEMPLATE_EXISTS_PATTERN =
      "Mail Template with action `%s` or ID `%s` already exists";
  private final MailTemplateLookupRepository lookupRepository;

  @Override
  public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
      List<? extends CommandMessage<?>> messages) {
    return (index, command) -> {
      String loggerPrefix = getLoggerPrefix("handle");

      if (CreateMailTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        CreateMailTemplateCommand createMailTemplateCommand =
            (CreateMailTemplateCommand) command.getPayload();

        var mailTemplateLookupEntity =
            lookupRepository.findByMailTemplateLookupIdOrMailAction(
                createMailTemplateCommand.getId(),
                createMailTemplateCommand.getEntity().getMailAction());

        if (mailTemplateLookupEntity != null) {
          throw new IllegalArgumentException(
              String.format(
                  MAIL_TEMPLATE_EXISTS_PATTERN,
                  createMailTemplateCommand.getEntity().getMailAction(),
                  createMailTemplateCommand.getId()));
        }
      } else if (UpdateMailTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        UpdateMailTemplateCommand updateMailTemplateCommand =
            (UpdateMailTemplateCommand) command.getPayload();

        var mailTemplateLookupEntity =
            lookupRepository.findByMailAction(
                updateMailTemplateCommand.getEntity().getMailAction());

        if (mailTemplateLookupEntity != null
            && !mailTemplateLookupEntity
                .getMailTemplateLookupId()
                .equals(updateMailTemplateCommand.getId())) {
          throw new IllegalArgumentException(
              String.format(
                  MAIL_TEMPLATE_EXISTS_PATTERN,
                  updateMailTemplateCommand.getEntity().getMailAction(),
                  updateMailTemplateCommand.getId()));
        }
      }
      return command;
    };
  }
}
