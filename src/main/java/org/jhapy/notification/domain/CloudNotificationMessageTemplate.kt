package org.jhapy.notification.domain

import lombok.Data
import lombok.EqualsAndHashCode
import lombok.ToString
import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Document
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
class CloudNotificationMessageTemplate : BaseEntity() {
    lateinit var name: String
    var title: String? = null
    var body: String? = null
    var data: String? = null
    var iso3Language: String? = null
    lateinit var cloudNotificationMessageAction: String
}