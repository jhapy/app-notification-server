package org.jhapy.notification.service;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.repository.SmsTemplateRepository;

@Service
@Transactional(readOnly = true)
public class SmsTemplateServiceImpl implements SmsTemplateService, HasLogger {

  private final SmsTemplateRepository smsTemplateRepository;

  public SmsTemplateServiceImpl(
      SmsTemplateRepository smsTemplateRepository) {
    this.smsTemplateRepository = smsTemplateRepository;
  }

  @Override
  public SmsTemplate getBySmsAction(String smsAction) {
    return smsTemplateRepository.findBySmsActionAndIsActiveIsTrue( smsAction ).orElse(null);
  }

  @Override
  public Page<SmsTemplate> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<SmsTemplate> result;

    if (StringUtils.isNotBlank(filter)) {
      result = smsTemplateRepository.findByCriteria(filter, pageable);
    } else {
      result = smsTemplateRepository.findAll(pageable);
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
      result = smsTemplateRepository.countByCriteria(filter);
    } else {
      result = smsTemplateRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  public long countBySmsAction(String smsAction) {
    return smsTemplateRepository.countBySmsActionAndIsActiveIsTrue(smsAction);
  }

  @Override
  public Optional<SmsTemplate> findBySmsAction(String smsAction, String iso3Language) {
    return smsTemplateRepository
        .findBySmsActionAndIso3LanguageAndIsActiveIsTrue(smsAction, iso3Language);
  }

  @Override
  @Transactional
  public SmsTemplate add(SmsTemplate smsTemplate) {
    return smsTemplateRepository.save(smsTemplate);
  }

  @Override
  @Transactional
  public SmsTemplate update(SmsTemplate smsTemplate) {
    return add(smsTemplate);
  }

  @Override
  @Transactional
  public void delete(SmsTemplate smsTemplate) {
    if (smsTemplate.getIsActive()) {
      smsTemplate.setIsActive(false);
      smsTemplateRepository.save(smsTemplate);
    } else {
      smsTemplateRepository.delete(smsTemplate);
    }
  }

  @Override
  public MongoRepository<SmsTemplate, String> getRepository() {
    return smsTemplateRepository;
  }
}
