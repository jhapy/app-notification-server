package org.jhapy.notification.config.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsTemplate;
import org.springframework.stereotype.Component;

@Component
@ChangeLog
public class InitialValuesChangeLog {

  @ChangeSet(order = "001", id = "createTransactionalCollections", author = "jHapy Dev1")
  public void createTransactionalCollections(MongockTemplate mongoTemplate) {
    if (!mongoTemplate.collectionExists("sms")) {
      mongoTemplate.createCollection(Sms.class);
    }
    if (!mongoTemplate.collectionExists("mail")) {
      mongoTemplate.createCollection(Mail.class);
    }
  }

  @ChangeSet(order = "002", id = "createTransactionalCollections2", author = "jHapy Dev1")
  public void createTransactionalCollections2(MongockTemplate mongoTemplate) {
    if (!mongoTemplate.collectionExists("mailTemplate")) {
      mongoTemplate.createCollection(MailTemplate.class);
    }
    if (!mongoTemplate.collectionExists("smsTemplate")) {
      mongoTemplate.createCollection(SmsTemplate.class);
    }
  }

  @ChangeSet(order = "003", id = "createTransactionalCollections3", author = "jHapy Dev1")
  public void createTransactionalCollections3(MongockTemplate mongoTemplate) {
    if (!mongoTemplate.collectionExists("cloudDataMessage")) {
      mongoTemplate.createCollection(CloudDataMessage.class);
    }
    if (!mongoTemplate.collectionExists("cloudNotificationMessage")) {
      mongoTemplate.createCollection(CloudNotificationMessage.class);
    }
    if (!mongoTemplate.collectionExists("cloudDataMessageTemplate")) {
      mongoTemplate.createCollection(CloudDataMessageTemplate.class);
    }
    if (!mongoTemplate.collectionExists("cloudNotificationMessageTemplate")) {
      mongoTemplate.createCollection(CloudNotificationMessageTemplate.class);
    }
  }
}
