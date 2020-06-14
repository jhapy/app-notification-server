package org.jhapy.notification.service;

import java.util.Optional;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CloudDataMessageTemplateService extends BaseCrudService<CloudDataMessageTemplate> {

  Page<CloudDataMessageTemplate> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  long countByCloudDataMessageAction(String cloudDataMessageAction);

  Optional<CloudDataMessageTemplate> findByCloudDataMessageAction(String cloudDataMessageAction,
      String iso3Language);

  CloudDataMessageTemplate add(CloudDataMessageTemplate cloudDataMessageTemplate);

  CloudDataMessageTemplate update(CloudDataMessageTemplate cloudDataMessageTemplate);

  void delete(CloudDataMessageTemplate cloudDataMessageTemplate);

  CloudDataMessageTemplate getByCloudDataMessageAction(String cloudDataMessageAction);
}
