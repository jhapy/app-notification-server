package org.jhapy.notification.service;

import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.repository.MailTemplateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MailTemplateServiceImpl implements MailTemplateService, HasLogger {

  private final MailTemplateRepository mailTemplateRepository;

  public MailTemplateServiceImpl(
      MailTemplateRepository mailTemplateRepository) {
    this.mailTemplateRepository = mailTemplateRepository;
  }

  @Override
  public MailTemplate getByMailAction(String mailAction) {
    return mailTemplateRepository.findByMailActionAndIsActiveIsTrue(mailAction).orElse(null);
  }

  @Override
  public Page<MailTemplate> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<MailTemplate> result;

    if (StringUtils.isNotBlank(filter)) {
      result = mailTemplateRepository.findByCriteria(filter, pageable);
    } else {
      result = mailTemplateRepository.findAll(pageable);
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
      result = mailTemplateRepository.countByCriteria(filter);
    } else {
      result = mailTemplateRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  public long countByMailAction(String mailAction) {
    return mailTemplateRepository.countByMailActionAndIsActiveIsTrue(mailAction);
  }

  @Override
  public Optional<MailTemplate> findByMailAction(String mailAction, String iso3Language) {
    if (StringUtils.isNotBlank(iso3Language)) {
      return mailTemplateRepository
          .findByMailActionAndIso3LanguageAndIsActiveIsTrue(mailAction, iso3Language);
    } else {
      return mailTemplateRepository
          .findByMailActionAndIsActiveIsTrue(mailAction);
    }
  }

  @Override
  @Transactional
  public MailTemplate add(MailTemplate mailTemplate) {
    return mailTemplateRepository.save(mailTemplate);
  }

  @Override
  @Transactional
  public MailTemplate update(MailTemplate mailTemplate) {
    return add(mailTemplate);
  }

  @Override
  @Transactional
  public void delete(MailTemplate mailTemplate) {
    if (mailTemplate.getIsActive()) {
      mailTemplate.setIsActive(false);
      mailTemplateRepository.save(mailTemplate);
    } else {
      mailTemplateRepository.delete(mailTemplate);
    }
  }

  @Override
  public MongoRepository<MailTemplate, String> getRepository() {
    return mailTemplateRepository;
  }
}
