package org.jhapy.notification.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsStatusEnum;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-22
 */
public interface SmsMessageRepository extends MongoRepository<Sms, String> {

  @Query("{'$and' : [{'$or' : [{'body' : {$regex : ?0, $options: 'i'}}, {'phoneNumber' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<Sms> findByCriteriaAndIsActive(String criteria, Boolean isShowInactive, Pageable pageable);

  @Query("{'$or' : [{'body' : {$regex : ?0, $options: 'i'}}, {'phoneNumber' : {$regex : ?0, $options: 'i'}}]}")
  Page<Sms> findByCriteria(String criteria, Pageable pageable);

  @Query(value = "{'$or' : [{'body' : {$regex : ?0, $options: 'i'}}, {'phoneNumber' : {$regex : ?0, $options: 'i'}}]}", count = true)
  long countByCriteria(String criteria);

  List<Sms> findBySmsStatusIn(SmsStatusEnum... smsStatusEnums);
}
