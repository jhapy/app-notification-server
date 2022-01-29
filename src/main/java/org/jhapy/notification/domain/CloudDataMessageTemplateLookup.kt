package org.jhapy.notification.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.io.Serializable
import java.util.*

@Document
class CloudDataMessageTemplateLookup(
    @Id val cloudDataMessageTemplateLookupId: UUID,
    var cloudDataMessageAction: String
) : Serializable