package org.jhapy.notification.repository

import org.jhapy.notification.domain.CloudNotificationMessageTemplateLookup
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CloudNotificationMessageTemplateLookupRepository :
    MongoRepository<CloudNotificationMessageTemplateLookup, UUID> {
    fun findByCloudNotificationMessageTemplateLookupIdOrCloudNotificationMessageAction(
        cloudNotificationMessageTemplateLookupId: UUID,
        cloudNotificationMessageAction: String
    ): CloudNotificationMessageTemplateLookup?

    fun findByCloudNotificationMessageAction(cloudNotificationMessageAction: String): CloudNotificationMessageTemplateLookup?
}