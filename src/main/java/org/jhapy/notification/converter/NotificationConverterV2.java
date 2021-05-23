package org.jhapy.notification.converter;

import java.util.Collection;
import java.util.List;
import org.jhapy.commons.converter.CommonsConverterV2;
import org.jhapy.dto.domain.notification.CloudDataMessage;
import org.jhapy.dto.domain.notification.CloudDataMessageTemplate;
import org.jhapy.dto.domain.notification.CloudNotificationMessage;
import org.jhapy.dto.domain.notification.CloudNotificationMessageTemplate;
import org.jhapy.dto.domain.notification.Mail;
import org.jhapy.dto.domain.notification.MailTemplate;
import org.jhapy.dto.domain.notification.Sms;
import org.jhapy.dto.domain.notification.SmsTemplate;
import org.mapstruct.Mapper;

/**
 * @author Alexandre Clavaud.
 * @version 1.0
 * @since 18/05/2021
 */
@Mapper(componentModel = "spring")
public abstract class NotificationConverterV2 extends CommonsConverterV2 {

  public abstract Mail convertToDto(org.jhapy.notification.domain.Mail domain);

  public abstract org.jhapy.notification.domain.Mail convertToDomain(Mail dto);

  public abstract List<Mail> convertToDtoMails(
      Collection<org.jhapy.notification.domain.Mail> domains);

  public abstract List<org.jhapy.notification.domain.Mail> convertToDomainMails(
      Collection<Mail> dtos);

  public abstract Sms convertToDto(org.jhapy.notification.domain.Sms domain);

  public abstract org.jhapy.notification.domain.Sms convertToDomain(Sms dto);

  public abstract List<Sms> convertToDtoSmss(Collection<org.jhapy.notification.domain.Sms> domains);

  public abstract List<org.jhapy.notification.domain.Sms> convertToDomainSmss(Collection<Sms> dtos);

  public abstract CloudDataMessage convertToDto(
      org.jhapy.notification.domain.CloudDataMessage domain);

  public abstract org.jhapy.notification.domain.CloudDataMessage convertToDomain(
      CloudDataMessage dto);

  public abstract List<CloudDataMessage> convertToDtoCloudDataMessages(
      Collection<org.jhapy.notification.domain.CloudDataMessage> domains);

  public abstract List<org.jhapy.notification.domain.CloudDataMessage> convertToDomainCloudDataMessages(
      Collection<CloudDataMessage> dtos);

  public abstract CloudNotificationMessage convertToDto(
      org.jhapy.notification.domain.CloudNotificationMessage domain);

  public abstract org.jhapy.notification.domain.CloudNotificationMessage convertToDomain(
      CloudNotificationMessage dto);

  public abstract List<CloudNotificationMessage> convertToDtoCloudNotificationMessages(
      Collection<org.jhapy.notification.domain.CloudNotificationMessage> domains);

  public abstract List<org.jhapy.notification.domain.CloudNotificationMessage> convertToDomainCloudNotificationMessages(
      Collection<CloudNotificationMessage> dtos);

  public abstract MailTemplate convertToDto(org.jhapy.notification.domain.MailTemplate domain);

  public abstract org.jhapy.notification.domain.MailTemplate convertToDomain(MailTemplate dto);

  public abstract List<MailTemplate> convertToDtoMailTemplates(
      Collection<org.jhapy.notification.domain.MailTemplate> domains);

  public abstract List<org.jhapy.notification.domain.MailTemplate> convertToDomainMailTemplates(
      Collection<MailTemplate> dtos);

  public abstract SmsTemplate convertToDto(org.jhapy.notification.domain.SmsTemplate domain);

  public abstract org.jhapy.notification.domain.SmsTemplate convertToDomain(SmsTemplate dto);

  public abstract List<SmsTemplate> convertToDtoSmsTemplates(
      Collection<org.jhapy.notification.domain.SmsTemplate> domains);

  public abstract List<org.jhapy.notification.domain.SmsTemplate> convertToDomainSmsTemplates(
      Collection<SmsTemplate> dtos);

  public abstract CloudDataMessageTemplate convertToDto(
      org.jhapy.notification.domain.CloudDataMessageTemplate domain);

  public abstract org.jhapy.notification.domain.CloudDataMessageTemplate convertToDomain(
      CloudDataMessageTemplate dto);

  public abstract List<CloudDataMessageTemplate> convertToDtoCloudDataMessageTemplates(
      Collection<org.jhapy.notification.domain.CloudDataMessageTemplate> domains);

  public abstract List<org.jhapy.notification.domain.CloudDataMessageTemplate> convertToDomainCloudDataMessageTemplates(
      Collection<CloudDataMessageTemplate> dtos);

  public abstract CloudNotificationMessageTemplate convertToDto(
      org.jhapy.notification.domain.CloudNotificationMessageTemplate domain);

  public abstract org.jhapy.notification.domain.CloudNotificationMessageTemplate convertToDomain(
      CloudNotificationMessageTemplate dto);

  public abstract List<CloudNotificationMessageTemplate> convertToDtoCloudNotificationMessageTemplates(
      Collection<org.jhapy.notification.domain.CloudNotificationMessageTemplate> domains);

  public abstract List<org.jhapy.notification.domain.CloudNotificationMessageTemplate> convertToDomainCloudNotificationMessageTemplates(
      Collection<CloudNotificationMessageTemplate> dtos);
}
