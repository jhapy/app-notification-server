package org.jhapy.notification.config.changelogs;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsTemplate;

@Component
@ChangeLog
public class InitialValuesChangeLog {

  @ChangeSet(order = "001", id = "createTransactionalCollections", author = "jHapy Dev1")
  public void createTransactionalCollections(MongoTemplate mongoTemplate) {
    mongoTemplate.createCollection(Sms.class);
    mongoTemplate.createCollection(Mail.class);
  }

  @ChangeSet(order = "002", id = "createTransactionalCollections2", author = "jHapy Dev1")
  public void createTransactionalCollections2(MongoTemplate mongoTemplate) {
    mongoTemplate.createCollection(MailTemplate.class);
    mongoTemplate.createCollection(SmsTemplate.class);
  }

  @ChangeSet(order = "003", id = "createTransactionalCollections3", author = "jHapy Dev1")
  public void createTransactionalCollections3(MongoTemplate mongoTemplate) {
    mongoTemplate.createCollection(CloudDataMessage.class);
    mongoTemplate.createCollection(CloudNotificationMessage.class);

    mongoTemplate.createCollection(CloudDataMessageTemplate.class);
    mongoTemplate.createCollection(CloudNotificationMessageTemplate.class);
  }
}
