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
import org.jhapy.cqrs.event.notification.CloudNotificationMessageCreatedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageDeletedEvent;
import org.jhapy.cqrs.event.notification.CloudNotificationMessageUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingCloudNotificationMessageQuery;
import org.jhapy.cqrs.query.notification.GetCloudNotificationMessageByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.client.CloudNotificationMessageProvider;
import org.jhapy.notification.converter.CloudNotificationMessageConverter;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.repository.CloudNotificationMessageRepository;
import org.jhapy.notification.repository.CloudNotificationMessageTemplateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
@ProcessingGroup("cloud-notification-message-group")
public class CloudNotificationMessageEventHandler implements HasLogger {
  private final CloudNotificationMessageRepository repository;
  private final CloudNotificationMessageTemplateRepository mailTemplateRepository;
  private final CloudNotificationMessageConverter converter;
  private final QueryUpdateEmitter queryUpdateEmitter;
  private final CloudNotificationMessageProvider cloudNotificationMessageProvider;

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
  public void on(CloudNotificationMessageCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onCloudNotificationMessageCreatedEvent");

    CloudNotificationMessage entity = converter.toEntity(event);
    entity = repository.save(entity);

    var optCloudNotificationMessageTemplate =
        mailTemplateRepository.findByCloudNotificationMessageActionAndIso3LanguageAndIsActiveIsTrue(
            event.getCloudNotificationMessageAction(), event.getIso3Language());

    if (optCloudNotificationMessageTemplate.isPresent()) {
      var template = optCloudNotificationMessageTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);

      if (StringUtils.isNotBlank(event.getDeviceToken())) {
        sendAndSave(
            event.getDeviceToken(),
            event.getTitle(),
            event.getBody(),
            event.getData(),
            template,
            event.getAttributes());

        queryUpdateEmitter.emit(
            GetCloudNotificationMessageByIdQuery.class,
            query -> true,
            converter.asDTO(entity, null));

        queryUpdateEmitter.emit(
            CountAnyMatchingCloudNotificationMessageQuery.class,
            query -> true,
            new CountChangeResult());
      } else {
        warn(loggerPrefix, "No cloudNotificationMessage to send.");
      }
    } else {
      error(loggerPrefix, "Template not found = {0}", event.getCloudNotificationMessageAction());
    }
  }

  @EventHandler
  public void on(CloudNotificationMessageUpdatedEvent event) throws Exception {
    CloudNotificationMessage entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(
        GetCloudNotificationMessageByIdQuery.class, query -> true, converter.asDTO(entity, null));
  }

  @EventHandler
  public void on(CloudNotificationMessageDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }

  private CloudNotificationMessageStatusEnum sendAndSave(
      String deviceToken,
      String title,
      String body,
      String data,
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate,
      Map<String, String> attributes) {
    var loggerPrefix = getLoggerPrefix("sendAndSave");
    trace(
        loggerPrefix,
        "Template = {0}, attributes = {1}",
        cloudNotificationMessageTemplate,
        attributes);
    var cloudNotificationMessage = new CloudNotificationMessage();
    cloudNotificationMessage.setCloudNotificationMessageStatus(
        CloudNotificationMessageStatusEnum.NOT_SENT);
    try {
      debug(loggerPrefix, "Building the message...");

      String _title = null;
      if (StringUtils.isNotBlank(title)) {
        _title = title;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getTitle())) {
          var titleTemplate =
              new Template(
                  null,
                  cloudNotificationMessageTemplate.getTitle(),
                  new Configuration(Configuration.VERSION_2_3_28));

          _title = FreeMarkerTemplateUtils.processTemplateIntoString(titleTemplate, attributes);
        }
      }
      String _body = null;

      if (StringUtils.isNotBlank(body)) {
        _body = body;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getBody())) {
          var bodyTemplate =
              new Template(
                  null,
                  cloudNotificationMessageTemplate.getBody(),
                  new Configuration(Configuration.VERSION_2_3_28));

          _body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);
        }
      }

      String _data = null;
      if (StringUtils.isNotBlank(data)) {
        _data = data;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getData())) {
          var dataTemplate =
              new Template(
                  null,
                  cloudNotificationMessageTemplate.getData(),
                  new Configuration(Configuration.VERSION_2_3_28));

          data = FreeMarkerTemplateUtils.processTemplateIntoString(dataTemplate, attributes);
        }
      }

      // initialize saved cloudNotificationMessage data
      cloudNotificationMessage.setDeviceToken(deviceToken);
      cloudNotificationMessage.setTitle(_title);
      cloudNotificationMessage.setBody(_body);
      cloudNotificationMessage.setData(_data);

      sendCloudNotificationMessage(cloudNotificationMessage);
    } catch (Exception e) {
      error(
          loggerPrefix,
          "Error while preparing mail = {0}, message = {1}",
          cloudNotificationMessage.getCloudNotificationMessageAction(),
          e.getMessage());
      cloudNotificationMessage.setCloudNotificationMessageStatus(
          CloudNotificationMessageStatusEnum.ERROR);
    } finally {
      debug(
          loggerPrefix,
          "Email sent status = {0}",
          cloudNotificationMessage.getCloudNotificationMessageStatus());
    }
    cloudNotificationMessage = repository.save(cloudNotificationMessage);
    return cloudNotificationMessage.getCloudNotificationMessageStatus();
  }

  private CloudNotificationMessageStatusEnum sendCloudNotificationMessage(
      CloudNotificationMessage cloudNotificationMessage) {
    var loggerPrefix = getLoggerPrefix("sendCloudNotificationMessage");
    debug(
        loggerPrefix,
        "Sending '{0}' to '{1}'",
        cloudNotificationMessage.getBody(),
        cloudNotificationMessage.getBody());
    var status =
        cloudNotificationMessageProvider.sendCloudNotificationMessage(
            cloudNotificationMessage.getDeviceToken(),
            cloudNotificationMessage.getTitle(),
            cloudNotificationMessage.getBody(),
            cloudNotificationMessage.getData(),
            cloudNotificationMessage.getId());

    debug(loggerPrefix, "Result = {0}", status);
    /*
    if (status.equals(CloudNotificationMessageResultCodeEnum.SENT)) {
      cloudNotificationMessage.setErrorMessage(null);
      cloudNotificationMessage.setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.SENT);
    } else {
      if (cloudNotificationMessage.getNbRetry() >= 3) {
        cloudNotificationMessage.setErrorMessage(status.name());
        cloudNotificationMessage.setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.ERROR);
      } else {
        cloudNotificationMessage.setErrorMessage(status.name());
        cloudNotificationMessage.setNbRetry(cloudNotificationMessage.getNbRetry() + 1);
        cloudNotificationMessage.setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.RETRYING);
      }
    }
     */
    cloudNotificationMessage = repository.save(cloudNotificationMessage);
    return cloudNotificationMessage.getCloudNotificationMessageStatus();
  }

  @Scheduled(cron = "${jhapy.tasks.cloudNotificationMessageQueueCronExpression}")
  public void processNotSentCloudNotificationMessages() {
    var unsentEcloudNotificationMessages =
        repository.findByCloudNotificationMessageStatusIn(
            CloudNotificationMessageStatusEnum.NOT_SENT,
            CloudNotificationMessageStatusEnum.RETRYING);
    unsentEcloudNotificationMessages.forEach(
        cloudNotificationMessage -> {
          sendCloudNotificationMessage(cloudNotificationMessage);
          repository.save(cloudNotificationMessage);
        });
  }
}
