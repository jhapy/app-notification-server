package org.jhapy.notification.query;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.interceptors.ExceptionHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.cqrs.event.notification.SmsCreatedEvent;
import org.jhapy.cqrs.event.notification.SmsDeletedEvent;
import org.jhapy.cqrs.event.notification.SmsUpdatedEvent;
import org.jhapy.cqrs.query.notification.CountAnyMatchingSmsQuery;
import org.jhapy.cqrs.query.notification.GetSmsByIdQuery;
import org.jhapy.dto.serviceQuery.CountChangeResult;
import org.jhapy.notification.client.SmsProvider;
import org.jhapy.notification.client.SmsResultCodeEnum;
import org.jhapy.notification.converter.SmsConverter;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsStatusEnum;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.repository.SmsMessageRepository;
import org.jhapy.notification.repository.SmsTemplateRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Component
@RequiredArgsConstructor
@ProcessingGroup("sms-group")
public class SmsEventHandler implements HasLogger {
  private final SmsMessageRepository repository;
  private final SmsTemplateRepository smsTemplateRepository;
  private final SmsConverter converter;
  private final QueryUpdateEmitter queryUpdateEmitter;
  private final SmsProvider smsProvider;

  @ExceptionHandler
  public void handleException(Exception ex) throws Exception {
    String loggerPrefix = getLoggerPrefix("handleException");
    error(
        loggerPrefix,
        ex,
        "Exception in EventHandler (ExceptionHandler): {0}:{1}",
        ex.getClass().getName(),
        ex.getMessage());
    throw ex;
  }

  @EventHandler
  public void on(SmsCreatedEvent event) throws Exception {
    String loggerPrefix = getLoggerPrefix("onSmsCreatedEvent");

    Sms entity = converter.toEntity(event);
    entity = repository.save(entity);

    var optSmsTemplate =
        smsTemplateRepository.findBySmsActionAndIso3LanguageAndIsActiveIsTrue(
            event.getSmsAction(), event.getIso3Language());

    if (optSmsTemplate.isPresent()) {
      var template = optSmsTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);

      if (StringUtils.isNotBlank(event.getPhoneNumber())) {
        sendAndSave(event.getPhoneNumber(), template, event.getAttributes());

        queryUpdateEmitter.emit(
            GetSmsByIdQuery.class, query -> true, converter.asDTO(entity, null));

        queryUpdateEmitter.emit(
            CountAnyMatchingSmsQuery.class, query -> true, new CountChangeResult());
      } else {
        warn(loggerPrefix, "No sms to send.");
      }
    } else {
      error(loggerPrefix, "Template not found = {0}", event.getSmsAction());
    }
  }

  @EventHandler
  public void on(SmsUpdatedEvent event) throws Exception {
    Sms entity = converter.toEntity(event);
    entity = repository.save(entity);
    queryUpdateEmitter.emit(GetSmsByIdQuery.class, query -> true, converter.asDTO(entity, null));
  }

  @EventHandler
  public void on(SmsDeletedEvent event) throws Exception {
    repository.deleteById(event.getId());
  }

  private SmsStatusEnum sendAndSave(
      String phoneNumber, SmsTemplate smsTemplate, Map<String, String> attributes) {
    var loggerPrefix = getLoggerPrefix("sendAndSave");
    trace(loggerPrefix, "Template = {0}, attributes = {1}", smsTemplate, attributes);
    var smsMessage = new Sms();
    smsMessage.setSmsStatus(SmsStatusEnum.NOT_SENT);
    try {
      debug(loggerPrefix, "Building the message...");

      var bodyTemplate =
          new Template(
              null, smsTemplate.getBody(), new Configuration(Configuration.VERSION_2_3_28));

      var body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);

      // initialize saved sms data
      smsMessage.setPhoneNumber(phoneNumber);
      smsMessage.setBody(body);

      sendSms(smsMessage);
    } catch (Exception e) {
      error(
          loggerPrefix,
          "Error while preparing mail = {0}, message = {1}",
          smsMessage.getSmsAction(),
          e.getMessage());
      smsMessage.setSmsStatus(SmsStatusEnum.ERROR);
    } finally {
      debug(loggerPrefix, "Email sent status = {0}", smsMessage.getSmsStatus());
    }
    smsMessage = repository.save(smsMessage);
    return smsMessage.getSmsStatus();
  }

  private SmsStatusEnum sendSms(Sms sms) {
    var loggerPrefix = getLoggerPrefix("sendSmsMessage");
    debug(loggerPrefix, "Sending '{0}' to '{1}'", sms.getBody(), sms.getBody());
    var status = smsProvider.sendSms(sms.getPhoneNumber(), sms.getBody(), sms.getId());
    if (status.equals(SmsResultCodeEnum.SENT)) {
      sms.setErrorMessage(null);
      sms.setSmsStatus(SmsStatusEnum.SENT);
    } else {
      if (sms.getNbRetry() >= 3) {
        sms.setErrorMessage(status.name());
        sms.setSmsStatus(SmsStatusEnum.ERROR);
      } else {
        sms.setErrorMessage(status.name());
        sms.setNbRetry(sms.getNbRetry() + 1);
        sms.setSmsStatus(SmsStatusEnum.RETRYING);
      }
    }
    sms = repository.save(sms);
    return sms.getSmsStatus();
  }

  @Scheduled(cron = "${jhapy.tasks.smsQueueCronExpression}")
  public void processNotSentSms() {
    var unsentEsmss = repository.findBySmsStatusIn(SmsStatusEnum.NOT_SENT, SmsStatusEnum.RETRYING);
    unsentEsmss.forEach(
        sms -> {
          sendSms(sms);
          repository.save(sms);
        });
  }
}
