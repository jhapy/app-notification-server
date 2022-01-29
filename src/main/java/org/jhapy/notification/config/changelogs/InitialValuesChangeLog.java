package org.jhapy.notification.config.changelogs;

import io.mongock.api.annotations.*;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.distributed.CommandDispatchException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.command.notification.CreateMailTemplateCommand;
import org.jhapy.cqrs.command.notification.CreateSmsTemplateCommand;
import org.jhapy.dto.domain.notification.MailActionEnum;
import org.jhapy.dto.domain.notification.MailTemplateDTO;
import org.jhapy.dto.domain.notification.SmsActionEnum;
import org.jhapy.dto.domain.notification.SmsTemplateDTO;
import org.jhapy.notification.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@ChangeUnit(order = "001", id = "initialValuesChangeLog", author = "jHapy Dev1")
public class InitialValuesChangeLog implements HasLogger {

  @BeforeExecution
  public void beforeExecution(MongoTemplate mongoTemplate) {

    mongoTemplate.createCollection(Sms.class);
    mongoTemplate.createCollection(Mail.class);
    mongoTemplate.createCollection(MailTemplate.class);
    mongoTemplate.createCollection(SmsTemplate.class);
    mongoTemplate.createCollection(CloudDataMessage.class);
    mongoTemplate.createCollection(CloudNotificationMessage.class);
    mongoTemplate.createCollection(CloudDataMessageTemplate.class);
    mongoTemplate.createCollection(CloudNotificationMessageTemplate.class);
  }

  @RollbackBeforeExecution
  public void rollbackBeforeExecution(MongoTemplate mongoTemplate) {

    mongoTemplate.dropCollection(Sms.class);
    mongoTemplate.dropCollection(Mail.class);
    mongoTemplate.dropCollection(MailTemplate.class);
    mongoTemplate.dropCollection(SmsTemplate.class);
    mongoTemplate.dropCollection(CloudDataMessage.class);
    mongoTemplate.dropCollection(CloudNotificationMessage.class);
    mongoTemplate.dropCollection(CloudDataMessageTemplate.class);
    mongoTemplate.dropCollection(CloudNotificationMessageTemplate.class);
  }

  @Execution
  public void execution(CommandGateway commandGateway) {
    String loggerPrefix = getLoggerPrefix("execution");
    {
      var emailVerificationMailTemplate = new MailTemplateDTO();
      emailVerificationMailTemplate.setMailAction(MailActionEnum.EMAIL_VERIFICATION.name());
      emailVerificationMailTemplate.setBody(
          "Hi,<br/>"
              + "<br/>"
              + "Thank you for signing up to JHapy. Please confirm your email address to activate your account.\n"
              + "<br/>"
              + "Verification code: ${verificationCode}");
      emailVerificationMailTemplate.setSubject("Welcome to JHapy");
      emailVerificationMailTemplate.setFrom("<JHapy> no-reply@jhapy.org");
      emailVerificationMailTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
      emailVerificationMailTemplate.setName("Verification Code");

      debug(loggerPrefix, "Start {0}", MailActionEnum.EMAIL_VERIFICATION.name());
      try {
        commandGateway.sendAndWait(new CreateMailTemplateCommand(emailVerificationMailTemplate));
      } catch (CommandExecutionException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Execution exception for {0} : {1}",
            MailActionEnum.EMAIL_VERIFICATION.name(),
            ex.getMessage());
      } catch (CommandDispatchException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Dispatch exception for {0} : {1}",
            MailActionEnum.EMAIL_VERIFICATION.name(),
            ex.getMessage());
      }
      debug(loggerPrefix, "End {0}", MailActionEnum.EMAIL_VERIFICATION.name());
    }
    {
      var emailForgetPasswordTemplate = new MailTemplateDTO();
      emailForgetPasswordTemplate.setMailAction(MailActionEnum.EMAIL_FORGET_PASSWORD.name());
      emailForgetPasswordTemplate.setBody("Hi,<br/>" + "<br/>" + "Reset code : ${resetCode}");
      emailForgetPasswordTemplate.setSubject("Password reset for JHapy");
      emailForgetPasswordTemplate.setFrom("<JHapy> no-reply@jhapy.org");
      emailForgetPasswordTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
      emailForgetPasswordTemplate.setName("Forget Password Code");

      debug(loggerPrefix, "Start {0}", MailActionEnum.EMAIL_FORGET_PASSWORD.name());
      try {
        commandGateway.sendAndWait(new CreateMailTemplateCommand(emailForgetPasswordTemplate));
      } catch (CommandExecutionException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Execution exception for {0} : {1}",
            MailActionEnum.EMAIL_FORGET_PASSWORD.name(),
            ex.getMessage());
      } catch (CommandDispatchException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Dispatch exception for {0} : {1}",
            MailActionEnum.EMAIL_FORGET_PASSWORD.name(),
            ex.getMessage());
      }
      debug(loggerPrefix, "End {0}", MailActionEnum.EMAIL_FORGET_PASSWORD.name());
    }
    {
      var smsVerificationSmsTemplate = new SmsTemplateDTO();
      smsVerificationSmsTemplate.setSmsAction(SmsActionEnum.SMS_VERIFICATION.name());
      smsVerificationSmsTemplate.setBody("Code jHapy ${verificationCode}");
      smsVerificationSmsTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
      smsVerificationSmsTemplate.setName("Verification Code");

      debug(loggerPrefix, "Start {0}", SmsActionEnum.SMS_VERIFICATION.name());
      try {
        commandGateway.sendAndWait(new CreateSmsTemplateCommand(smsVerificationSmsTemplate));
      } catch (CommandExecutionException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Execution exception for {0} : {1}",
            SmsActionEnum.SMS_VERIFICATION.name(),
            ex.getMessage());
      } catch (CommandDispatchException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Dispatch exception for {0} : {1}",
            SmsActionEnum.SMS_VERIFICATION.name(),
            ex.getMessage());
      }
      debug(loggerPrefix, "End {0}", SmsActionEnum.SMS_VERIFICATION.name());
    }
    {
      var smsForgetPasswordSmsTemplate = new SmsTemplateDTO();
      smsForgetPasswordSmsTemplate.setSmsAction(SmsActionEnum.SMS_FORGET_PASSWORD.name());
      smsForgetPasswordSmsTemplate.setBody("Reset Code for JHapy : ${resetCode}");
      smsForgetPasswordSmsTemplate.setIso3Language(Locale.ENGLISH.getLanguage());
      smsForgetPasswordSmsTemplate.setName("Forget Password Code");

      debug(loggerPrefix, "Start {0}", SmsActionEnum.SMS_FORGET_PASSWORD.name());
      try {
        commandGateway.sendAndWait(new CreateSmsTemplateCommand(smsForgetPasswordSmsTemplate));
      } catch (CommandExecutionException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Execution exception for {0} : {1}",
            SmsActionEnum.SMS_FORGET_PASSWORD.name(),
            ex.getMessage());
      } catch (CommandDispatchException ex) {
        error(
            loggerPrefix,
            ex,
            "Command Dispatch exception for {0} : {1}",
            SmsActionEnum.SMS_FORGET_PASSWORD.name(),
            ex.getMessage());
      }
      debug(loggerPrefix, "End {0}", SmsActionEnum.SMS_FORGET_PASSWORD.name());

      debug(loggerPrefix, "End");
    }
  }

  @RollbackExecution
  public void rollbackExecution(CommandGateway commandGateway) {}
}
