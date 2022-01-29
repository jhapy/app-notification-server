package org.jhapy.notification.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.command.notification.CreateSmsTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateSmsTemplateCommand;
import org.jhapy.notification.repository.SmsTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CreateOrUpdateSmsTemplateCommandInterceptor
    implements MessageDispatchInterceptor<CommandMessage<?>>, HasLogger {
  static final String SMS_TEMPLATE_EXISTS_PATTERN =
      "Sms Template with action `%s` or ID `%s` already exists";
  private final SmsTemplateLookupRepository lookupRepository;

  @Override
  public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
      List<? extends CommandMessage<?>> messages) {
    return (index, command) -> {
      String loggerPrefix = getLoggerPrefix("handle");

      if (CreateSmsTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        CreateSmsTemplateCommand createSmsTemplateCommand =
            (CreateSmsTemplateCommand) command.getPayload();

        var smsTemplateLookupEntity =
            lookupRepository.findBySmsTemplateLookupIdOrSmsAction(
                createSmsTemplateCommand.getId(),
                createSmsTemplateCommand.getEntity().getSmsAction());

        if (smsTemplateLookupEntity != null) {
          throw new IllegalArgumentException(
              String.format(
                  SMS_TEMPLATE_EXISTS_PATTERN,
                  createSmsTemplateCommand.getEntity().getSmsAction(),
                  createSmsTemplateCommand.getId()));
        }
      } else if (UpdateSmsTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        UpdateSmsTemplateCommand updateSmsTemplateCommand =
            (UpdateSmsTemplateCommand) command.getPayload();

        var smsTemplateLookupEntity =
            lookupRepository.findBySmsAction(updateSmsTemplateCommand.getEntity().getSmsAction());

        if (smsTemplateLookupEntity != null
            && !smsTemplateLookupEntity
                .getSmsTemplateLookupId()
                .equals(updateSmsTemplateCommand.getId())) {
          throw new IllegalArgumentException(
              String.format(
                  SMS_TEMPLATE_EXISTS_PATTERN,
                  updateSmsTemplateCommand.getEntity().getSmsAction(),
                  updateSmsTemplateCommand.getId()));
        }
      }
      return command;
    };
  }
}
