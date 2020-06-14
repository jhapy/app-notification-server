package org.jhapy.notification.service;

import java.util.Optional;
import org.jhapy.notification.domain.MailTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MailTemplateService extends BaseCrudService<MailTemplate> {

  Page<MailTemplate> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  long countByMailAction(String mailAction);

  Optional<MailTemplate> findByMailAction(String mailAction, String iso3Language);

  MailTemplate add(MailTemplate mailTemplate);

  MailTemplate update(MailTemplate mailTemplate);

  void delete(MailTemplate mailTemplate);

  MailTemplate getByMailAction(String mailAction);
}
