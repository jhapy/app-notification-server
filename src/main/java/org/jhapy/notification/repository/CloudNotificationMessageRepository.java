package org.jhapy.notification.repository;

import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.UUID;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-22
 */
public interface CloudNotificationMessageRepository
    extends MongoRepository<CloudNotificationMessage, UUID> {

  @Query(
      "{'$and' : [{'$or' : [{'title' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}, {'deviceToken' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<CloudNotificationMessage> findByCriteriaAndIsActive(
      String criteria, Boolean isShowInactive, Pageable pageable);

  @Query(
      "{'$or' : [{'title' : {$regex : ?0, $options: 'i'}}, {'deviceToken' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}")
  Page<CloudNotificationMessage> findByCriteria(String criteria, Pageable pageable);

  @Query(
      value =
          "{'$or' : [{'title' : {$regex : ?0, $options: 'i'}}, {'deviceToken' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}",
      count = true)
  Long countByCriteria(String criteria);

  List<CloudNotificationMessage> findByCloudNotificationMessageStatusIn(
      CloudNotificationMessageStatusEnum... mailStatus);
}