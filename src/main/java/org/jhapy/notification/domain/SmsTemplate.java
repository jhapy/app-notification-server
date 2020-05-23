package org.jhapy.notification.domain;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-22
 */
@Document
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SmsTemplate extends BaseEntity {

  @NotNull
  private String name;

  @NotNull
  private String body;

  private String iso3Language;

  @NotNull
  private String smsAction;
}
