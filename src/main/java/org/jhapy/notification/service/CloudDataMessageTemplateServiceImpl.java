package org.jhapy.notification.service;

import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.repository.CloudDataMessageTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class CloudDataMessageTemplateServiceImpl
    implements CloudDataMessageTemplateService, HasLogger {

  private final CloudDataMessageTemplateRepository cloudDataMessageTemplateRepository;

  public CloudDataMessageTemplateServiceImpl(
      CloudDataMessageTemplateRepository cloudDataMessageTemplateRepository) {
    this.cloudDataMessageTemplateRepository = cloudDataMessageTemplateRepository;
  }

  @Override
  public CloudDataMessageTemplate getByCloudDataMessageAction(String cloudDataMessageAction) {
    return cloudDataMessageTemplateRepository
        .findByCloudDataMessageActionAndIsActiveIsTrue(cloudDataMessageAction)
        .orElse(null);
  }

  @Override
  public Page<CloudDataMessageTemplate> findAnyMatching(String filter, Pageable pageable) {
    var loggerString = getLoggerPrefix("findAnyMatching");
    debug(loggerString, "In = {0}", filter);
    Page<CloudDataMessageTemplate> result;

    if (StringUtils.isNotBlank(filter)) {
      result = cloudDataMessageTemplateRepository.findByCriteria(filter, pageable);
    } else {
      result = cloudDataMessageTemplateRepository.findAll(pageable);
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
      result = cloudDataMessageTemplateRepository.countByCriteria(filter);
    } else {
      result = cloudDataMessageTemplateRepository.count();
    }

    debug(loggerString, "Out = {0}", result);
    return result;
  }

  @Override
  public long countByCloudDataMessageAction(String cloudDataMessageAction) {
    return cloudDataMessageTemplateRepository.countByCloudDataMessageActionAndIsActiveIsTrue(
        cloudDataMessageAction);
  }

  @Override
  public Optional<CloudDataMessageTemplate> findByCloudDataMessageAction(
      String cloudDataMessageAction, String iso3Language) {
    return cloudDataMessageTemplateRepository
        .findByCloudDataMessageActionAndIso3LanguageAndIsActiveIsTrue(
            cloudDataMessageAction, iso3Language);
  }

  @Override
  @Transactional
  public CloudDataMessageTemplate add(CloudDataMessageTemplate cloudDataMessageTemplate) {
    return cloudDataMessageTemplateRepository.save(cloudDataMessageTemplate);
  }

  @Override
  @Transactional
  public CloudDataMessageTemplate update(CloudDataMessageTemplate cloudDataMessageTemplate) {
    return add(cloudDataMessageTemplate);
  }

  @Override
  @Transactional
  public void delete(CloudDataMessageTemplate cloudDataMessageTemplate) {
    if (cloudDataMessageTemplate.getIsActive()) {
      cloudDataMessageTemplate.setIsActive(false);
      cloudDataMessageTemplateRepository.save(cloudDataMessageTemplate);
    } else {
      cloudDataMessageTemplateRepository.delete(cloudDataMessageTemplate);
    }
  }

  @Override
  public MongoRepository<CloudDataMessageTemplate, UUID> getRepository() {
    return cloudDataMessageTemplateRepository;
  }
}