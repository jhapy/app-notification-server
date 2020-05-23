package org.jhapy.notification.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;


public interface CloudNotificationMessageTemplateService extends
    BaseCrudService<CloudNotificationMessageTemplate> {

  Page<CloudNotificationMessageTemplate> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  long countByCloudNotificationMessageAction(String cloudNotificationMessageAction);

  Optional<CloudNotificationMessageTemplate> findByCloudNotificationMessageAction(
      String cloudNotificationMessageAction, String iso3Language);

  CloudNotificationMessageTemplate add(
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate);

  CloudNotificationMessageTemplate update(
      CloudNotificationMessageTemplate cloudNotificationMessageTemplate);

  void delete(CloudNotificationMessageTemplate cloudNotificationMessageTemplate);

  CloudNotificationMessageTemplate getByCloudNotificationMessageAction(
      String cloudNotificationMessageAction);
}
