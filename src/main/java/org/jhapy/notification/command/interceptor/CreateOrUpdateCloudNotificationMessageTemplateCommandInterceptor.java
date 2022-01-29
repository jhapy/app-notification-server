package org.jhapy.notification.command.interceptor;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.command.notification.CreateCloudNotificationMessageTemplateCommand;
import org.jhapy.cqrs.command.notification.UpdateCloudNotificationMessageTemplateCommand;
import org.jhapy.notification.repository.CloudNotificationMessageTemplateLookupRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class CreateOrUpdateCloudNotificationMessageTemplateCommandInterceptor
    implements MessageDispatchInterceptor<CommandMessage<?>>, HasLogger {
  static final String CLOUD_NOTIFICATION_MESSAGE_TEMPLATE_EXISTS_PATTERN =
      "CloudNotificationMessage Template with action `%s` or ID `%s` already exists";
  private final CloudNotificationMessageTemplateLookupRepository lookupRepository;

  @Override
  public BiFunction<Integer, CommandMessage<?>, CommandMessage<?>> handle(
      List<? extends CommandMessage<?>> messages) {
    return (index, command) -> {
      String loggerPrefix = getLoggerPrefix("handle");

      if (CreateCloudNotificationMessageTemplateCommand.class.equals(command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        CreateCloudNotificationMessageTemplateCommand
            createCloudNotificationMessageTemplateCommand =
                (CreateCloudNotificationMessageTemplateCommand) command.getPayload();

        var cloudNotificationMessageTemplateLookupEntity =
            lookupRepository
                .findByCloudNotificationMessageTemplateLookupIdOrCloudNotificationMessageAction(
                    createCloudNotificationMessageTemplateCommand.getId(),
                    createCloudNotificationMessageTemplateCommand
                        .getEntity()
                        .getCloudNotificationMessageAction());

        if (cloudNotificationMessageTemplateLookupEntity != null) {
          throw new IllegalArgumentException(
              String.format(
                  CLOUD_NOTIFICATION_MESSAGE_TEMPLATE_EXISTS_PATTERN,
                  createCloudNotificationMessageTemplateCommand
                      .getEntity()
                      .getCloudNotificationMessageAction(),
                  createCloudNotificationMessageTemplateCommand.getId()));
        }
      } else if (UpdateCloudNotificationMessageTemplateCommand.class.equals(
          command.getPayloadType())) {
        debug(loggerPrefix, "Intercepted command type: {0}", command.getPayloadType());
        UpdateCloudNotificationMessageTemplateCommand
            updateCloudNotificationMessageTemplateCommand =
                (UpdateCloudNotificationMessageTemplateCommand) command.getPayload();

        var cloudNotificationMessageTemplateLookupEntity =
            lookupRepository.findByCloudNotificationMessageAction(
                updateCloudNotificationMessageTemplateCommand
                    .getEntity()
                    .getCloudNotificationMessageAction());

        if (cloudNotificationMessageTemplateLookupEntity != null
            && !cloudNotificationMessageTemplateLookupEntity
                .getCloudNotificationMessageTemplateLookupId()
                .equals(updateCloudNotificationMessageTemplateCommand.getId())) {
          throw new IllegalArgumentException(
              String.format(
                  CLOUD_NOTIFICATION_MESSAGE_TEMPLATE_EXISTS_PATTERN,
                  updateCloudNotificationMessageTemplateCommand
                      .getEntity()
                      .getCloudNotificationMessageAction(),
                  updateCloudNotificationMessageTemplateCommand.getId()));
        }
      }
      return command;
    };
  }
}
