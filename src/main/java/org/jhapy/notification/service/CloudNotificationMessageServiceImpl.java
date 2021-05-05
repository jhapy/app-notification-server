package org.jhapy.notification.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.client.CloudNotificationMessageProvider;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.repository.CloudNotificationMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
@Service
@Transactional(readOnly = true)
public class CloudNotificationMessageServiceImpl implements CloudNotificationMessageService,
    HasLogger {

  private final CloudNotificationMessageProvider cloudNotificationMessageProvider;

  private final CloudNotificationMessageRepository cloudNotificationMessageRepository;

  private final CloudNotificationMessageTemplateService cloudNotificationMessageTemplateService;

  public CloudNotificationMessageServiceImpl(
      CloudNotificationMessageProvider cloudNotificationMessageProvider,
      CloudNotificationMessageRepository cloudNotificationMessageRepository,
      CloudNotificationMessageTemplateService cloudNotificationMessageTemplateService) {
    this.cloudNotificationMessageProvider = cloudNotificationMessageProvider;
    this.cloudNotificationMessageRepository = cloudNotificationMessageRepository;
    this.cloudNotificationMessageTemplateService = cloudNotificationMessageTemplateService;
  }

  @Override
  public void sendSimpleCloudNotificationMessage(String deviceToken, String title, String body,
      String data) {
    cloudNotificationMessageProvider
        .sendCloudNotificationMessage(deviceToken, title, body, data, UUID.randomUUID().toString());
  }

  @Override
  public Page<CloudNotificationMessage> findAnyMatching(String filter, Pageable pageable) {
    var loggerString = getLoggerPrefix("findAnyMatching");
    debug(loggerString, "In = {0}", filter);
    Page<CloudNotificationMessage> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudNotificationMessageRepository.findAll(pageable);
    }

    debug(loggerString, "Out = {0}", result);

    return result;
  }


  @Override
  public long countAnyMatching(String filter) {
    var loggerString = getLoggerPrefix("countAnyMatching");
    debug(loggerString, "In = {0}", filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageRepository.countByCriteria(filter);
    } else {
      result = cloudNotificationMessageRepository.count();
    }

    debug(loggerString, "Out = {0}", result);
    return result;
  }

  @Override
  @Transactional
  public CloudNotificationMessageStatusEnum sendCloudNotificationMessage(String deviceToken,
      String cloudNotificationMessageAction, String title, String body, String data,
      Map<String, String> attributes, String iso3Language) {
    var loggerPrefix = getLoggerPrefix("sendCloudNotificationMessage");

    var _cloudNotificationMessageTemplate = cloudNotificationMessageTemplateService
        .findByCloudNotificationMessageAction(cloudNotificationMessageAction, iso3Language);

    CloudNotificationMessageTemplate template = null;
    if (_cloudNotificationMessageTemplate.isPresent()) {
      template = _cloudNotificationMessageTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);
    }

    if (StringUtils.isNotBlank(deviceToken)) {
      return sendAndSave(deviceToken, title, body, data, template, attributes);
    } else {
      warn(loggerPrefix, "No cloudNotificationMessage to send.");
    }
    return null;
  }

  private CloudNotificationMessageStatusEnum sendAndSave(String deviceToken, String title,
      String body, String data, CloudNotificationMessageTemplate cloudNotificationMessageTemplate,
      Map<String, String> attributes) {
    var loggerPrefix = getLoggerPrefix("sendAndSave");
    trace(loggerPrefix, "Template = {0}, attributes = {1}", cloudNotificationMessageTemplate,
        attributes);
    var cloudNotificationMessage = new CloudNotificationMessage();
    cloudNotificationMessage
        .setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.NOT_SENT);
    try {
      debug(loggerPrefix, "Building the message...");

      String _title = null;
      if (StringUtils.isNotBlank(title)) {
        _title = title;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getTitle())) {
          var titleTemplate = new Template(
              null,
              cloudNotificationMessageTemplate.getTitle(),
              new Configuration(Configuration.VERSION_2_3_28)
          );

          _title = FreeMarkerTemplateUtils.processTemplateIntoString(titleTemplate, attributes);
        }
      }
      String _body = null;

      if (StringUtils.isNotBlank(body)) {
        _body = body;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getBody())) {
          var bodyTemplate = new Template(
              null,
              cloudNotificationMessageTemplate.getBody(),
              new Configuration(Configuration.VERSION_2_3_28)
          );

          _body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);
        }
      }

      String _data = null;
      if (StringUtils.isNotBlank(data)) {
        _data = data;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getData())) {
          var dataTemplate = new Template(
              null,
              cloudNotificationMessageTemplate.getData(),
              new Configuration(Configuration.VERSION_2_3_28)
          );

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
      error(loggerPrefix, "Error while preparing mail = {0}, message = {1}",
          cloudNotificationMessage.getCloudNotificationMessageAction(), e.getMessage());
      cloudNotificationMessage
          .setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.ERROR);
    } finally {
      debug(loggerPrefix, "Email sent status = {0}",
          cloudNotificationMessage.getCloudNotificationMessageStatus());
    }
    cloudNotificationMessage = cloudNotificationMessageRepository.save(cloudNotificationMessage);
    return cloudNotificationMessage.getCloudNotificationMessageStatus();
  }

  private CloudNotificationMessageStatusEnum sendCloudNotificationMessage(
      CloudNotificationMessage cloudNotificationMessage) {
    var loggerPrefix = getLoggerPrefix("sendCloudNotificationMessage");
    debug(loggerPrefix, "Sending '{0}' to '{1}'", cloudNotificationMessage.getBody(),
        cloudNotificationMessage.getBody());
    var status = cloudNotificationMessageProvider
        .sendCloudNotificationMessage(cloudNotificationMessage.getDeviceToken(),
            cloudNotificationMessage.getTitle(), cloudNotificationMessage.getBody(),
            cloudNotificationMessage.getData(), cloudNotificationMessage.getId());

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
    cloudNotificationMessage = cloudNotificationMessageRepository.save(cloudNotificationMessage);
    return cloudNotificationMessage.getCloudNotificationMessageStatus();
  }

  @Override
  @Transactional
  public void processNotSentCloudNotificationMessages() {
    var unsentEcloudNotificationMessages = cloudNotificationMessageRepository
        .findByCloudNotificationMessageStatusIn(CloudNotificationMessageStatusEnum.NOT_SENT,
            CloudNotificationMessageStatusEnum.RETRYING);
    unsentEcloudNotificationMessages.forEach(cloudNotificationMessage -> {
      sendCloudNotificationMessage(cloudNotificationMessage);
      cloudNotificationMessageRepository.save(cloudNotificationMessage);
    });
  }

  @Override
  public MongoRepository<CloudNotificationMessage, String> getRepository() {
    return cloudNotificationMessageRepository;
  }
}
