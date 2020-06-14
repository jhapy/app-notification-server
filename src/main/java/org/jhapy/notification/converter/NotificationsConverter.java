package org.jhapy.notification.converter;

import org.jhapy.commons.utils.OrikaBeanMapper;
import org.jhapy.notification.domain.CloudDataMessage;
import org.jhapy.notification.domain.CloudDataMessageTemplate;
import org.jhapy.notification.domain.CloudNotificationMessage;
import org.jhapy.notification.domain.CloudNotificationMessageTemplate;
import org.jhapy.notification.domain.Mail;
import org.jhapy.notification.domain.MailTemplate;
import org.jhapy.notification.domain.Sms;
import org.jhapy.notification.domain.SmsTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-06-05
 */
@Component
public class NotificationsConverter {

  private final OrikaBeanMapper orikaBeanMapper;

  public NotificationsConverter(OrikaBeanMapper orikaBeanMapper) {
    this.orikaBeanMapper = orikaBeanMapper;
  }

  @Bean
  public void notificationsConverters() {
    orikaBeanMapper
        .addMapper(Mail.class, org.jhapy.dto.domain.notification.Mail.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.Mail.class, Mail.class);

    orikaBeanMapper
        .addMapper(Sms.class, org.jhapy.dto.domain.notification.Sms.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.Sms.class, Sms.class);

    orikaBeanMapper
        .addMapper(CloudDataMessage.class,
            org.jhapy.dto.domain.notification.CloudDataMessage.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.CloudDataMessage.class,
            CloudDataMessage.class);

    orikaBeanMapper
        .addMapper(CloudNotificationMessage.class,
            org.jhapy.dto.domain.notification.CloudNotificationMessage.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.CloudNotificationMessage.class,
            CloudNotificationMessage.class);

    orikaBeanMapper
        .addMapper(MailTemplate.class,
            org.jhapy.dto.domain.notification.MailTemplate.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.MailTemplate.class,
            MailTemplate.class);

    orikaBeanMapper
        .addMapper(SmsTemplate.class,
            org.jhapy.dto.domain.notification.SmsTemplate.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.SmsTemplate.class,
            SmsTemplate.class);

    orikaBeanMapper
        .addMapper(CloudDataMessageTemplate.class,
            org.jhapy.dto.domain.notification.CloudDataMessageTemplate.class);
    orikaBeanMapper
        .addMapper(org.jhapy.dto.domain.notification.CloudDataMessageTemplate.class,
            CloudDataMessageTemplate.class);

    orikaBeanMapper
        .addMapper(CloudNotificationMessageTemplate.class,
            org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate.class);
    orikaBeanMapper
        .addMapper(
            org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate.class,
            CloudNotificationMessageTemplate.class);
  }
}