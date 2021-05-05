package org.jhapy.notification.service;

import java.util.Optional;
import org.jhapy.notification.domain.SmsTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface SmsTemplateService extends BaseCrudService<SmsTemplate> {

  Page<SmsTemplate> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  long countBySmsAction(String smsAction);

  Optional<SmsTemplate> findBySmsAction(String smsAction, String iso3Language);

  SmsTemplate add(SmsTemplate smsTemplate);

  SmsTemplate update(SmsTemplate smsTemplate);

  @Override
  void delete(SmsTemplate smsTemplate);

  SmsTemplate getBySmsAction(String smsAction);
}
