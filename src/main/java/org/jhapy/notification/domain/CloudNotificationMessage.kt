package org.jhapy.notification.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Document
class CloudNotificationMessage : BaseEntity() {
    var deviceToken: String? = null
    var title: String? = null
    var body: String? = null
    var data: String? = null
    var cloudNotificationMessageAction: String? = null
    var cloudNotificationMessageStatus: CloudNotificationMessageStatusEnum? = null
    var errorMessage: String? = null
    var nbRetry = 0
    var applicationName: String? = null
    var iso3Language: String? = null
}

enum class CloudNotificationMessageStatusEnum {
    NOT_SENT, SENT, ERROR, RETRYING
}
