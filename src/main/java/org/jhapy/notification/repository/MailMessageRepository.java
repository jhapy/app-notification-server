package org.jhapy.notification.repository;

import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailStatusEnum;
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
public interface MailMessageRepository extends MongoRepository<Mail, UUID> {

  @Query(
      "{'$and' : [{'$or' : [{'subject' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}, {'to' : {$regex : ?0, $options: 'i'}}]}, {'isActive' : ?1}]}")
  Page<Mail> findByCriteriaAndIsActive(String criteria, Boolean isShowInactive, Pageable pageable);

  @Query(
      "{'$or' : [{'subject' : {$regex : ?0, $options: 'i'}}, {'to' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}")
  Page<Mail> findByCriteria(String criteria, Pageable pageable);

  @Query(
      value =
          "{'$or' : [{'subject' : {$regex : ?0, $options: 'i'}}, {'to' : {$regex : ?0, $options: 'i'}}, {'body' : {$regex : ?0, $options: 'i'}}]}",
      count = true)
  Long countByCriteria(String criteria);

  List<Mail> findByMailStatusIn(MailStatusEnum... mailStatus);
}
