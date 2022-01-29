package org.jhapy.notification.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.command.notification.CreateCloudDataMessageTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateCloudDataMessageTemplateCommand;
import org.jhapy.notification.repository.CloudDataMessageTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CreateOrUpdateCloudDataMessageTemplateCommandInterceptor
    implements MessageDispatchInterceptor<CommandMessage<?>>, HasLogger {
  static final String CLOUD_DATA_MESSAGE_TEMPLATE_EXISTS_PATTERN =
      "CloudDataMessage Template with action `%s` or ID `%s` already exists";
  private final CloudDataMessageTemplateLookupRepository lookupRepository;

  @Override
  public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
      List<? extends CommandMessage<?>> messages) {
    return (index, command) -> {
      String loggerPrefix = getLoggerPrefix("handle");

      if (CreateCloudDataMessageTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        CreateCloudDataMessageTemplateCommand createCloudDataMessageTemplateCommand =
            (CreateCloudDataMessageTemplateCommand) command.getPayload();

        var cloudDataMessageTemplateLookupEntity =
            lookupRepository.findByCloudDataMessageTemplateLookupIdOrCloudDataMessageAction(
                createCloudDataMessageTemplateCommand.getId(),
                createCloudDataMessageTemplateCommand.getEntity().getCloudDataMessageAction());

        if (cloudDataMessageTemplateLookupEntity != null) {
          throw new IllegalArgumentException(
              String.format(
                  CLOUD_DATA_MESSAGE_TEMPLATE_EXISTS_PATTERN,
                  createCloudDataMessageTemplateCommand.getEntity().getCloudDataMessageAction(),
                  createCloudDataMessageTemplateCommand.getId()));
        }
      } else if (UpdateCloudDataMessageTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        UpdateCloudDataMessageTemplateCommand updateCloudDataMessageTemplateCommand =
            (UpdateCloudDataMessageTemplateCommand) command.getPayload();

        var cloudDataMessageTemplateLookupEntity =
            lookupRepository.findByCloudDataMessageAction(
                updateCloudDataMessageTemplateCommand.getEntity().getCloudDataMessageAction());

        if (cloudDataMessageTemplateLookupEntity != null
            && !cloudDataMessageTemplateLookupEntity
                .getCloudDataMessageTemplateLookupId()
                .equals(updateCloudDataMessageTemplateCommand.getId())) {
          throw new IllegalArgumentException(
              String.format(
                  CLOUD_DATA_MESSAGE_TEMPLATE_EXISTS_PATTERN,
                  updateCloudDataMessageTemplateCommand.getEntity().getCloudDataMessageAction(),
                  updateCloudDataMessageTemplateCommand.getId()));
        }
      }
      return command;
    };
  }
}
