package org.jhapy.notification.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document
@ToString(callSuper = true)
public class Sms extends BaseEntity {

  private String phoneNumber;

  private String body;

  private String smsAction;

  private SmsStatusEnum smsStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private String applicationName;

  private String iso3Language;
}
