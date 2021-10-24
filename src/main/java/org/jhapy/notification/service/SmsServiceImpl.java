package org.jhapy.notification.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.client.SmsProvider;
import org.jhapy.notification.client.SmsResultCodeEnum;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsStatusEnum;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.repository.SmsMessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
@Service
@Transactional(readOnly = true)
public class SmsServiceImpl implements SmsService, HasLogger {

  private final SmsProvider smsProvider;

  private final SmsMessageRepository smsMessageRepository;

  private final SmsTemplateService smsTemplateService;

  public SmsServiceImpl(
      SmsProvider smsProvider,
      SmsMessageRepository smsMessageRepository,
      SmsTemplateService smsTemplateService) {
    this.smsProvider = smsProvider;
    this.smsMessageRepository = smsMessageRepository;
    this.smsTemplateService = smsTemplateService;
  }

  @Override
  public void sendSimpleSms(String to, String text) {
    smsProvider.sendSms(to, text, UUID.randomUUID());
  }

  @Override
  public Page<Sms> findAnyMatching(String filter, Pageable pageable) {
    var loggerString = getLoggerPrefix("findAnyMatching");
    debug(loggerString, "In = {0}", filter);
    Page<Sms> result;

    if (StringUtils.isNotBlank(filter)) {
      result = smsMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = smsMessageRepository.findAll(pageable);
    }

    debug(loggerString, "Out = {0}", result);

    return result;
  }

  @Override
  public long countAnyMatching(String filter) {
    var loggerString = getLoggerPrefix("countAnyMatching");
    debug(loggerString, "In = {0}", filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = smsMessageRepository.countByCriteria(filter);
    } else {
      result = smsMessageRepository.count();
    }

    debug(loggerString, "Out = {0}", result);
    return result;
  }

  @Override
  @Transactional
  public SmsStatusEnum sendSms(
      String phoneNumber, String smsAction, Map<String, String> attributes, String iso3Language) {
    var loggerPrefix = getLoggerPrefix("sendSms");

    var optSmsTemplate = smsTemplateService.findBySmsAction(smsAction, iso3Language);

    if (optSmsTemplate.isPresent()) {
      var template = optSmsTemplate.get();
      trace(loggerPrefix, "Template found = {0}", template);

      if (StringUtils.isNotBlank(phoneNumber)) {
        return sendAndSave(phoneNumber, template, attributes);
      } else {
        warn(loggerPrefix, "No sms to send.");
      }
    }
    return null;
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
    smsMessage = smsMessageRepository.save(smsMessage);
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
    sms = smsMessageRepository.save(sms);
    return sms.getSmsStatus();
  }

  @Override
  @Transactional
  public void processNotSentSms() {
    var unsentEsmss =
        smsMessageRepository.findBySmsStatusIn(SmsStatusEnum.NOT_SENT, SmsStatusEnum.RETRYING);
    unsentEsmss.forEach(
        sms -> {
          sendSms(sms);
          smsMessageRepository.save(sms);
        });
  }

  @Override
  public MongoRepository<Sms, UUID> getRepository() {
    return smsMessageRepository;
  }
}