package org.jhapy.notification.repository;

import java.util.Optional;
import org.jhapy.notification.domain.SmsTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-22
 */
public interface SmsTemplateRepository extends MongoRepository<SmsTemplate, String> {

  @Query("{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<SmsTemplate> findByCriteriaAndIsActive(String criteria, Boolean isShowInactive,
      Pageable pageable);

  @Query(value = "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}")
  Page<SmsTemplate> findByCriteria(String criteria, Pageable pageable);

  Page<SmsTemplate> findByIsActive(Boolean isActive, Pageable page);

  @Query(value = "{'$and' : [{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}", count = true)
  Long countByCriteriaAndIsActive(String criteria, Boolean isActive);

  @Query(value = "{'$or' : [{'name' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}", count = true)
  Long countByCriteria(String criteria);


  long countByIsActive(Boolean isActive);

  long countBySmsActionAndIsActiveIsTrue(String smsAction);

  Optional<SmsTemplate> findBySmsActionAndIso3LanguageAndIsActiveIsTrue(
      String smsAction, String iso3Language);

  Optional<SmsTemplate> findBySmsActionAndIsActiveIsTrue(String smsAction);
}
