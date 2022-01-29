package org.jhapy.notification.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Document
class CloudDataMessage : BaseEntity() {
    var deviceToken: String? = null
    var data: String? = null
    var topic: String? = null
    var cloudDataMessageAction: String? = null
    var cloudDataMessageStatus: CloudDataMessageStatusEnum? = null
    var errorMessage: String? = null
    var nbRetry = 0
    var applicationName: String? = null
    var iso3Language: String? = null
}

enum class CloudDataMessageStatusEnum {
    NOT_SENT, SENT, ERROR, RETRYING
}