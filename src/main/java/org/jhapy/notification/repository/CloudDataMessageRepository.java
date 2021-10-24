package org.jhapy.notification.repository;

import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.domain.CloudDataMessageStatusEnum;
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
public interface CloudDataMessageRepository extends MongoRepository<CloudDataMessage, UUID> {

  @Query(
      "{'$and' : [{'$or' : [{'data' : {$regex : ?0, $options: 'i'}}, {'deviceToken' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<CloudDataMessage> findByCriteriaAndIsActive(
      String criteria, Boolean isShowInactive, Pageable pageable);

  @Query(
      "{'$or' : [{'deviceToken' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}")
  Page<CloudDataMessage> findByCriteria(String criteria, Pageable pageable);

  @Query(
      value =
          "{'$or' : [{'deviceToken' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}",
      count = true)
  Long countByCriteria(String criteria);

  List<CloudDataMessage> findByCloudDataMessageStatusIn(CloudDataMessageStatusEnum... mailStatus);
}