package org.jhapy.notification.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.client.CloudNotificationMessageProvider;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.repository.CloudNotificationMessageRepository;

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
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<CloudNotificationMessage> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudNotificationMessageRepository.findAll(pageable);
    }

    logger().debug(loggerString + "Out = " + result);

    return result;
  }


  @Override
  public long countAnyMatching(String filter) {
    String loggerString = getLoggerPrefix("countAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageRepository.countByCriteria(filter);
    } else {
      result = cloudNotificationMessageRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  @Transactional
  public CloudNotificationMessageStatusEnum sendCloudNotificationMessage(String deviceToken,
      String cloudNotificationMessageAction, String title, String body, String data,
      Map<String, String> attributes, String iso3Language) {
    String loggerPrefix = getLoggerPrefix("sendCloudNotificationMessage");

    Optional<CloudNotificationMessageTemplate> _cloudNotificationMessageTemplate = cloudNotificationMessageTemplateService
        .findByCloudNotificationMessageAction(cloudNotificationMessageAction, iso3Language);

    CloudNotificationMessageTemplate template = null;
    if (_cloudNotificationMessageTemplate.isPresent()) {
      template = _cloudNotificationMessageTemplate.get();
      logger().trace(loggerPrefix + "Template found = {}", template);
    }

    if (StringUtils.isNotBlank(deviceToken)) {
      return sendAndSave(deviceToken, title, body, data, template, attributes);
    } else {
      logger().warn(loggerPrefix + "No cloudNotificationMessage to send.");
    }
    return null;
  }

  private CloudNotificationMessageStatusEnum sendAndSave(String deviceToken, String title,
      String body, String data, CloudNotificationMessageTemplate cloudNotificationMessageTemplate,
      Map<String, String> attributes) {
    String loggerPrefix = getLoggerPrefix("sendAndSave");
    logger()
        .trace(loggerPrefix + "Template = {}, attributes = {}", cloudNotificationMessageTemplate,
            attributes);
    CloudNotificationMessage cloudNotificationMessage = new CloudNotificationMessage();
    cloudNotificationMessage
        .setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.NOT_SENT);
    try {
      logger().debug(loggerPrefix + "Building the message...");

      String _title = null;
      if (StringUtils.isNotBlank(title)) {
        _title = title;
      } else if (cloudNotificationMessageTemplate != null) {
        if (StringUtils.isNotBlank(cloudNotificationMessageTemplate.getTitle())) {
          Template titleTemplate = new Template(
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
          Template bodyTemplate = new Template(
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
          Template dataTemplate = new Template(
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
      logger().error(loggerPrefix + "Error while preparing mail = {}, message = {}",
          cloudNotificationMessage.getCloudNotificationMessageAction(), e.getMessage());
      cloudNotificationMessage
          .setCloudNotificationMessageStatus(CloudNotificationMessageStatusEnum.ERROR);
    } finally {
      logger().debug(loggerPrefix + "Email sent status = {}",
          cloudNotificationMessage.getCloudNotificationMessageStatus());
    }
    cloudNotificationMessage = cloudNotificationMessageRepository.save(cloudNotificationMessage);
    return cloudNotificationMessage.getCloudNotificationMessageStatus();
  }

  private CloudNotificationMessageStatusEnum sendCloudNotificationMessage(
      CloudNotificationMessage cloudNotificationMessage) {
    String loggerPrefix = getLoggerPrefix("sendCloudNotificationMessage");
    logger().debug(loggerPrefix + "Sending '" + cloudNotificationMessage.getBody() + "' to "
        + cloudNotificationMessage.getBody());
    String status = cloudNotificationMessageProvider
        .sendCloudNotificationMessage(cloudNotificationMessage.getDeviceToken(),
            cloudNotificationMessage.getTitle(), cloudNotificationMessage.getBody(),
            cloudNotificationMessage.getData(), cloudNotificationMessage.getId());

    logger().debug(loggerPrefix + "Result = " + status);
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
    List<CloudNotificationMessage> unsentEcloudNotificationMessages = cloudNotificationMessageRepository
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
