package org.jhapy.notification.repository;

import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;
import java.util.UUID;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-22
 */
public interface CloudDataMessageTemplateRepository
    extends MongoRepository<CloudDataMessageTemplate, UUID> {

  @Query(
      "{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<CloudDataMessageTemplate> findByCriteriaAndIsActive(
      String criteria, Boolean isShowInactive, Pageable pageable);

  @Query(
      value =
          "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}")
  Page<CloudDataMessageTemplate> findByCriteria(String criteria, Pageable pageable);

  Page<CloudDataMessageTemplate> findByIsActive(Boolean isActive, Pageable page);

  @Query(
      value =
          "{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}",
      count = true)
  Long countByCriteriaAndIsActive(String criteria, Boolean isActive);

  @Query(
      value =
          "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'data' : {$regex : ?0, $options: 'i'}}]}",
      count = true)
  Long countByCriteria(String criteria);

  long countByIsActive(Boolean isActive);

  long countByCloudDataMessageActionAndIsActiveIsTrue(String cloudDataMessageAction);

  Optional<CloudDataMessageTemplate> findByCloudDataMessageActionAndIso3LanguageAndIsActiveIsTrue(
      String cloudDataMessageAction, String iso3Language);

  Optional<CloudDataMessageTemplate> findByCloudDataMessageActionAndIsActiveIsTrue(
      String cloudDataMessageAction);
}
