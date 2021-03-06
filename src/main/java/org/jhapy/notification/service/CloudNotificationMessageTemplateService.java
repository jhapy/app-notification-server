package org.jhapy.notification.service;

import java.util.Optional;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


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
