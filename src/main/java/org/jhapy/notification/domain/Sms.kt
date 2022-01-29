package org.jhapy.notification.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Document
class Sms : BaseEntity() {
    var phoneNumber: String? = null
    var body: String? = null
    var smsAction: String? = null
    var smsStatus: SmsStatusEnum? = null
    var errorMessage: String? = null
    var nbRetry = 0
    var applicationName: String? = null
    var iso3Language: String? = null
}

enum class SmsStatusEnum {
    NOT_SENT, SENT, ERROR, RETRYING
}