package org.jhapy.notification.query;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.CloudDataMessageCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudDataMessageUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingCloudDataMessageQuery;
import org.jhapy.cqrs.query.notification.GetCloudDataMessageByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.client.CloudDataMessageProvider;
import org.jhapy.notification.converter.CloudDataMessageConverter;
import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.domain.CloudDataMessageStatusEnum;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.repository.CloudDataMessageRepository;
import org.jhapy.notification.repository.CloudDataMessageTemplateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@ProcessingGroup("cloud-data-message-group")
public class CloudDataMessageEventHandler implements HasLogger {
  private final CloudDataMessageRepository repository;
  private final CloudDataMessageTemplateRepository mailTemplateRepository;
  private final CloudDataMessageConverter converter;
  private final QueryUpdateEmitter queryUpdateEmitter;
  private final CloudDataMessageProvider cloudDataMessageProvider;

  @ExceptionHandler
  public void handleException(Exception ex) throws Exception {
    String loggerPrefix = getLoggerPrefix("handleException");
    error(
        loggerPrefix,
        ex,
        "Exception in EventHandler (ExceptionHandler): {0}:{1}",
        ex.getClass().getName(),
        ex.getMessage());
    throw ex;
  }

  @EventHandler
  public void on(CloudDataMessageCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onCloudDataMessageCreatedEvent");

    CloudDataMessage entity = converter.toEntity(event);
    entity = repository.save(entity);

    var optCloudDataMessageTemplate =
        mailTemplateRepository.findByCloudDataMessageActionAndIso3LanguageAndIsActiveIsTrue(
            event.getCloudDataMessageAction(), event.getIso3Language());

    if (optCloudDataMessageTemplate.isPresent()) {
      var template = optCloudDataMessageTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);

      if (StringUtils.isNotBlank(event.getDeviceToken())
          || StringUtils.isNotBlank(event.getTopic())) {
        sendAndSave(
            event.getDeviceToken(),
            event.getTopic(),
            event.getData(),
            template,
            event.getAttributes());

        queryUpdateEmitter.emit(
            GetCloudDataMessageByIdQuery.class, query -> true, converter.asDTO(entity, null));

        queryUpdateEmitter.emit(
            CountAnyMatchingCloudDataMessageQuery.class, query -> true, new CountChangeResult());
      } else {
        warn(loggerPrefix, "No cloudDataMessage to send.");
      }
    } else {
      error(loggerPrefix, "Template not found = {0}", event.getCloudDataMessageAction());
    }
  }

  @EventHandler
  public void on(CloudDataMessageUpdatedEvent event) throws Exception {
    CloudDataMessage entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetCloudDataMessageByIdQuery.class, query -> true, converter.asDTO(entity, null));
  }

  @EventHandler
  public void on(CloudDataMessageDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }

  private CloudDataMessageStatusEnum sendAndSave(
      String deviceToken,
      String topic,
      String data,
      CloudDataMessageTemplate cloudDataMessageTemplate,
      Map<String, String> attributes) {
    var loggerPrefix = getLoggerPrefix("sendAndSave");
    trace(loggerPrefix, "Template = {0}, attributes = ", cloudDataMessageTemplate, attributes);
    var cloudDataMessage = new CloudDataMessage();
    cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.NOT_SENT);
    try {
      debug(loggerPrefix, "Building the message...");

      String _data = null;
      if (StringUtils.isNotBlank(data)) {
        _data = data;
      } else if (cloudDataMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudDataMessageTemplate.getData())) {
          var dataTemplate =
              new Template(
                  null,
                  cloudDataMessageTemplate.getData(),
                  new Configuration(Configuration.VERSION_2_3_28));

          data = FreeMarkerTemplateUtils.processTemplateIntoString(dataTemplate, attributes);
        }
      }
      // initialize saved cloudDataMessage data
      cloudDataMessage.setDeviceToken(deviceToken);
      cloudDataMessage.setData(_data);
      cloudDataMessage.setTopic(topic);
      sendCloudDataMessage(cloudDataMessage);
    } catch (Exception e) {
      error(
          loggerPrefix,
          "Error while preparing mail = {0}, message = {1}",
          cloudDataMessage.getCloudDataMessageAction(),
          e.getMessage());
      cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.ERROR);
    } finally {
      debug(loggerPrefix, "Email sent status = {0}", cloudDataMessage.getCloudDataMessageStatus());
    }
    cloudDataMessage = repository.save(cloudDataMessage);
    return cloudDataMessage.getCloudDataMessageStatus();
  }

  private CloudDataMessageStatusEnum sendCloudDataMessage(CloudDataMessage cloudDataMessage) {
    var loggerPrefix = getLoggerPrefix("sendCloudDataMessage");
    debug(
        loggerPrefix,
        "Sending '{0}' to '{1}'",
        cloudDataMessage.getData(),
        cloudDataMessage.getDeviceToken());
    var status =
        cloudDataMessageProvider.sendCloudDataMessage(
            cloudDataMessage.getDeviceToken(),
            cloudDataMessage.getTopic(),
            cloudDataMessage.getData(),
            cloudDataMessage.getId());
    debug(loggerPrefix, "Result = {0}", status);

    /*
    if (status.equals(CloudDataMessageResultCodeEnum.SENT)) {
      cloudDataMessage.setErrorMessage(null);
      cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.SENT);
    } else {
      if (cloudDataMessage.getNbRetry() >= 3) {
        cloudDataMessage.setErrorMessage(status.name());
        cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.ERROR);
      } else {
        cloudDataMessage.setErrorMessage(status.name());
        cloudDataMessage.setNbRetry(cloudDataMessage.getNbRetry() + 1);
        cloudDataMessage.setCloudDataMessageStatus(CloudDataMessageStatusEnum.RETRYING);
      }
    }
     */
    cloudDataMessage = repository.save(cloudDataMessage);
    return cloudDataMessage.getCloudDataMessageStatus();
  }

  @Scheduled(cron = "${jhapy.tasks.cloudDataMessageQueueCronExpression}")
  public void processNotSentCloudDataMessages() {
    List<CloudDataMessage> unsentCloudDataMessages =
        repository.findByCloudDataMessageStatusIn(
            CloudDataMessageStatusEnum.NOT_SENT, CloudDataMessageStatusEnum.RETRYING);
    unsentCloudDataMessages.forEach(
        cloudDataMessage -> {
          sendCloudDataMessage(cloudDataMessage);
          repository.save(cloudDataMessage);
        });
  }

}
