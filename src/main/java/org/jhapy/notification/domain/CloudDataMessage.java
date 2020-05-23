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
@Document
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CloudDataMessage extends BaseEntity {

  private String deviceToken;

  private String data;

  private String topic;

  private String cloudDataMessageAction;

  private CloudDataMessageStatusEnum cloudDataMessageStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private String applicationName;

  private String iso3Language;
}
