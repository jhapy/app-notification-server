package org.jhapy.notification.repository;

import org.jhapy.notification.domain.MailTemplate;
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
public interface MailTemplateRepository extends MongoRepository<MailTemplate, UUID> {

  @Query(
      "{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}},{'subject' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<MailTemplate> findByCriteriaAndIsActive(
      String criteria, Boolean isShowInactive, Pageable pageable);

  @Query(
      "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}},{'subject' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}")
  Page<MailTemplate> findByCriteria(String criteria, Pageable pageable);

  Page<MailTemplate> findByIsActive(Boolean isActive, Pageable page);

  @Query(
      value =
          "{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}},{'subject' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}",
      count = true)
  Long countByCriteriaAndIsActive(String criteria, Boolean isActive);

  @Query(
      value =
          "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}},{'subject' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}",
      count = true)
  Long countByCriteria(String criteria);

  long countByIsActive(Boolean isActive);

  long countByMailActionAndIsActiveIsTrue(String mailAction);

  Optional<MailTemplate> findByMailActionAndIso3LanguageAndIsActiveIsTrue(
      String mailAction, String iso3Language);

  Optional<MailTemplate> findByMailActionAndIsActiveIsTrue(String mailAction);
}
