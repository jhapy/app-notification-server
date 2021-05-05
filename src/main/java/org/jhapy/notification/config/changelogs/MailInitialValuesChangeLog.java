package org.jhapy.notification.config.changelogs;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import java.util.Locale;
import org.jhapy.dto.domain.notification.MailActionEnum;
import org.jhapy.dto.domain.notification.SmsActionEnum;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.domain.SmsTemplate;
import org.springframework.stereotype.Component;

@Component
@ChangeLog
public class MailInitialValuesChangeLog {

  @ChangeSet(order = "001", id = "insertVerificationCodeEmail", author = "jHapy Dev1")
  public void insertVerificationCodeEmail(MongockTemplate mongoTemplate) {
    var notificationMailTemplate = new MailTemplate();
    notificationMailTemplate.setMailAction(MailActionEnum.EMAIL_VERIFICATION.name());
    notificationMailTemplate.setBody("Hi,<br/>"
        + "<br/>"
        + "Thank you for signing up to JHapy. Please confirm your email address to activate your account.\n"
        + "<br/>"
        + "Verification code: ${verificationCode}");
    notificationMailTemplate.setSubject("Welcome to JHapy");
    notificationMailTemplate.setFrom("<JHapy> no-reply@jhapy.org");
    notificationMailTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
    notificationMailTemplate.setName("Verification Code");

    mongoTemplate.save(notificationMailTemplate);
  }

  @ChangeSet(order = "002", id = "insertVerificationCodeSms", author = "jHapy Dev1")
  public void insertVerificationCodeSms(MongockTemplate mongoTemplate) {
    var notificationSmsTemplate = new SmsTemplate();
    notificationSmsTemplate.setSmsAction(SmsActionEnum.SMS_VERIFICATION.name());
    notificationSmsTemplate.setBody("Code jHapy ${verificationCode}");
    notificationSmsTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
    notificationSmsTemplate.setName("Verification Code");

    mongoTemplate.save(notificationSmsTemplate);
  }

  @ChangeSet(order = "003", id = "insertForgetPasswordEmail", author = "jHapy Dev1")
  public void insertForgetPasswordEmail(MongockTemplate mongoTemplate) {
    var notificationMailTemplate = new MailTemplate();
    notificationMailTemplate.setMailAction(MailActionEnum.EMAIL_FORGET_PASSWORD.name());
    notificationMailTemplate.setBody("Hi,<br/>"
        + "<br/>"
        + "Reset code : ${resetCode}");
    notificationMailTemplate.setSubject("Password reset for JHapy");
    notificationMailTemplate.setFrom("<JHapy> no-reply@jhapy.org");
    notificationMailTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
    notificationMailTemplate.setName("Forget Password Code");

    mongoTemplate.save(notificationMailTemplate);
  }

  @ChangeSet(order = "004", id = "insertForgetPasswordSms", author = "jHapy Dev1")
  public void insertForgetPasswordSms(MongockTemplate mongoTemplate) {
    var notificationSmsTemplate = new SmsTemplate();
    notificationSmsTemplate.setSmsAction(SmsActionEnum.SMS_FORGET_PASSWORD.name());
    notificationSmsTemplate.setBody("Reset Code for JHapy : ${resetCode}");
    notificationSmsTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
    notificationSmsTemplate.setName("Forget Password Code");

    mongoTemplate.save(notificationSmsTemplate);
  }

  @ChangeSet(order = "005", id = "createTransactionalCollections", author = "jHapy Dev1")
  public void createTransactionalCollections(MongockTemplate mongoTemplate) {
    //mongoTemplate.createCollection(Sms.class);
    //mongoTemplate.createCollection(Mail.class);

  }
}
