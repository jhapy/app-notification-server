package org.jhapy.notification.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.jhapy.commons.utils.HasLogger;
import org.jhapy.notification.client.SmsProvider;
import org.jhapy.notification.client.SmsResultCodeEnum;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsStatusEnum;
import org.jhapy.notification.domain.SmsTemplate;
import org.jhapy.notification.repository.SmsMessageRepository;

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

  public SmsServiceImpl(SmsProvider smsProvider,
      SmsMessageRepository smsMessageRepository,
      SmsTemplateService smsTemplateService) {
    this.smsProvider = smsProvider;
    this.smsMessageRepository = smsMessageRepository;
    this.smsTemplateService = smsTemplateService;
  }

  @Override
  public void sendSimpleSms(String to, String text) {
    smsProvider.sendSms(to, text, UUID.randomUUID().toString());
  }

  @Override
  public Page<Sms> findAnyMatching(String filter, Pageable pageable) {
    String loggerString = getLoggerPrefix("findAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    Page<Sms> result;

    if (StringUtils.isNotBlank(filter)) {
      result = smsMessageRepository.findByCriteria(filter, pageable);
    } else {
      result = smsMessageRepository.findAll(pageable);
    }

    logger().debug(loggerString + "Out = " + result);

    return result;
  }


  @Override
  public long countAnyMatching(String filter) {
    String loggerString = getLoggerPrefix("countAnyMatching");
    logger().debug(loggerString + "In = " + filter);
    long result;
    if (StringUtils.isNotBlank(filter)) {
      result = smsMessageRepository.countByCriteria(filter);
    } else {
      result = smsMessageRepository.count();
    }

    logger().debug(loggerString + "Out = " + result);
    return result;
  }

  @Override
  @Transactional
  public SmsStatusEnum sendSms(String phoneNumber, String smsAction, Map<String, String> attributes,
      String iso3Language) {
    String loggerPrefix = getLoggerPrefix("sendSms");

    Optional<SmsTemplate> _smsTemplate = smsTemplateService
        .findBySmsAction(smsAction, iso3Language);

    if (_smsTemplate.isPresent()) {
      SmsTemplate template = _smsTemplate.get();
      logger().trace(loggerPrefix + "Template found = {}", template);

      if (StringUtils.isNotBlank(phoneNumber)) {
        return sendAndSave(phoneNumber, template, attributes);
      } else {
        logger().warn(loggerPrefix + "No sms to send.");
      }
    }
    return null;
  }

  private SmsStatusEnum sendAndSave(String phoneNumber, SmsTemplate smsTemplate,
      Map<String, String> attributes) {
    String loggerPrefix = getLoggerPrefix("sendAndSave");
    logger().trace(loggerPrefix + "Template = {}, attributes = {}", smsTemplate, attributes);
    Sms smsMessage = new Sms();
    smsMessage.setSmsStatus(SmsStatusEnum.NOT_SENT);
    try {
      logger().debug(loggerPrefix + "Building the message...");

      Template bodyTemplate = new Template(
          null,
          smsTemplate.getBody(),
          new Configuration(Configuration.VERSION_2_3_28)
      );

      String body = FreeMarkerTemplateUtils.processTemplateIntoString(bodyTemplate, attributes);

      // initialize saved sms data
      smsMessage.setPhoneNumber(phoneNumber);
      smsMessage.setBody(body);

      sendSms(smsMessage);
    } catch (Exception e) {
      logger().error(loggerPrefix + "Error while preparing mail = {}, message = {}",
          smsMessage.getSmsAction(), e.getMessage());
      smsMessage.setSmsStatus(SmsStatusEnum.ERROR);
    } finally {
      logger().debug(loggerPrefix + "Email sent status = {}", smsMessage.getSmsStatus());
    }
    smsMessage = smsMessageRepository.save(smsMessage);
    return smsMessage.getSmsStatus();
  }

  private SmsStatusEnum sendSms(Sms sms) {
    String loggerPrefix = getLoggerPrefix("sendSmsMessage");
    logger().debug(loggerPrefix + "Sending '" + sms.getBody() + "' to " + sms.getBody());
    SmsResultCodeEnum status = smsProvider
        .sendSms(sms.getPhoneNumber(), sms.getBody(), sms.getId());
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
    List<Sms> unsentEsmss = smsMessageRepository
        .findBySmsStatusIn(SmsStatusEnum.NOT_SENT, SmsStatusEnum.RETRYING);
    unsentEsmss.forEach(sms -> {
      sendSms(sms);
      smsMessageRepository.save(sms);
    });
  }

  @Override
  public MongoRepository<Sms, String> getRepository() {
    return smsMessageRepository;
  }
}
