package org.jhapy.notification.service;


import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsStatusEnum;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-04
 */
public interface SmsService extends BaseCrudService<Sms> {

  Page<Sms> findAnyMatching(String filter, Pageable pageable);

  long countAnyMatching(String filter);

  void sendSimpleSms(String to, String text);

  void processNotSentSms();

  SmsStatusEnum sendSms(String phoneNumber, String smsAction, Map<String, String> attributes,
      String iso3Language);
}
