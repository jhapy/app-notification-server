package org.jhapy.notification.client;

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-10
 */
public enum SmsResultCodeEnum {
  SENT, MISSING_PARAMETER, INCORRECT_ID, INCORRECT_SHORT_CODE, INSUFFISANT_CREDIT, ERROR_5XX, ERROR_4XX, ERROR_OTHER, UNKNOWN
}
