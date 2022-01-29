package org.jhapy.notification.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Document
class Mail : BaseEntity() {
    var to: String? = null
    var copyTo: String? = null
    var from: String? = null
    var subject: String? = null
    var body: String? = null
    var attachments: Map<String, ByteArray>? = null
    var mailAction: String? = null
    var mailStatus: MailStatusEnum? = null
    var errorMessage: String? = null
    var nbRetry = 0
    var applicationName: String? = null
    var iso3Language: String? = null
}

enum class MailStatusEnum {
    NOT_SENT, SENT, ERROR, RETRYING
}
