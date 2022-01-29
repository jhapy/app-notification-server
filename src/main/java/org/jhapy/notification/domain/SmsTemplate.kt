package org.jhapy.notification.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-02-22
 */
@Document
class SmsTemplate : BaseEntity() {
    lateinit var name: String
    lateinit var body: String
    var iso3Language: String? = null
    lateinit var smsAction: String
}