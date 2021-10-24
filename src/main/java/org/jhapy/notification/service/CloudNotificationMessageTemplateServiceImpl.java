package org.jhapy.notification.service;

import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.repository.CloudNotificationMessageTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CloudNotificationMessageTemplateServiceImpl
    implements CloudNotificationMessageTemplateService, HasLogger {

  private final CloudNotificationMessageTemplateRepository
      cloudNotificationMessageTemplateRepository;

  public CloudNotificationMessageTemplateServiceImpl(
      CloudNotificationMessageTemplateRepository cloudNotificationMessageTemplateRepository) {
    this.cloudNotificationMessageTemplateRepository = cloudNotificationMessageTemplateRepository;
  }

  @Override
  public CloudNotificationMessageTemplate getByCloudNotificationMessageAction(
      String cloudNotificationMessageAction) {
    return cloudNotificationMessageTemplateRepository
        .findByCloudNotificationMessageActionAndIsActiveIsTrue(cloudNotificationMessageAction)
        .orElse(null);
  }

  @Override
  public Page<CloudNotificationMessageTemplate> findAnyMatching(String filter, Pageable pageable) {
    var loggerString = getLoggerPrefix("findAnyMatching");
    debug(loggerString, "In = " + filter);
    Page<CloudNotificationMessageTemplate> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageTemplateRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudNotificationMessageTemplateRepository.findAll(pageable);
    }

    debug(loggerString, "Out = " + result);

    return result;
  }

  @Override
  public long countAnyMatching(String filter) {
    var loggerString = getLoggerPrefix("countAnyMatching");
    debug(loggerString, "In = " + filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = cloudNotificationMessageTemplateRepository.countByCriteria(filter);
    } else {
      result = cloudNotificationMessageTemplateRepository.count();
    }

    debug(loggerString, "Out = " + result);
    return result;
  }

  @Override
  public long countByCloudNotificationMessageAction(String cloudNotificationMessageAction) {
    return cloudNotificationMessageTemplateRepository
        .countByCloudNotificationMessageActionAndIsActiveIsTrue(cloudNotificationMessageAction);
  }

  @Override
  public Optional<CloudNotificationMessageTemplate> findByCloudNotificationMessageAction(
      String cloudNotificationMessageAction, String iso3Language) {
    return cloudNotificationMessageTemplateRepository
        .findByCloudNotificationMessageActionAndIso3LanguageAndIsActiveIsTrue(
            cloudNotificationMessageAction, iso3Language);
  }

  @Override
  @Transactional
  public CloudNotificationMessageTemplate add(
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate) {
    return cloudNotificationMessageTemplateRepository.save(cloudNotificationMessageTemplate);
  }

  @Override
  @Transactional
  public CloudNotificationMessageTemplate update(
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate) {
    return add(cloudNotificationMessageTemplate);
  }

  @Override
  @Transactional
  public void delete(CloudNotificationMessageTemplate cloudNotificationMessageTemplate) {
    if (cloudNotificationMessageTemplate.getIsActive()) {
      cloudNotificationMessageTemplate.setIsActive(false);
      cloudNotificationMessageTemplateRepository.save(cloudNotificationMessageTemplate);
    } else {
      cloudNotificationMessageTemplateRepository.delete(cloudNotificationMessageTemplate);
    }
  }

  @Override
  public MongoRepository<CloudNotificationMessageTemplate, UUID> getRepository() {
    return cloudNotificationMessageTemplateRepository;
  }
}