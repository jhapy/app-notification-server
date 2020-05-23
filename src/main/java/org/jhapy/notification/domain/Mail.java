package org.jhapy.notification.domain;

import java.util.Map;
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
public class Mail extends BaseEntity {

  private String to;

  private String copyTo;

  private String from;

  private String subject;

  private String body;

  private Map<String, byte[]> attachements;

  private String mailAction;

  private MailStatusEnum mailStatus;

  private String errorMessage;

  private int nbRetry = 0;

  private String applicationName;

  private String iso3Language;
}
