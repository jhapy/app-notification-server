package org.jhapy.notification.domain

import org.springframework.data.mongodb.core.mapping.Document

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-07-09
 */
@Document
class CloudDataMessageTemplate : BaseEntity() {
    lateinit var name: String
    lateinit var data: String
    var iso3Language: String? = null
    lateinit var cloudDataMessageAction: String
}