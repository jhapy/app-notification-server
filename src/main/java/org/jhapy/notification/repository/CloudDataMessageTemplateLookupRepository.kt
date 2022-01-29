package org.jhapy.notification.repository

import org.jhapy.notification.domain.CloudDataMessageTemplateLookup
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface CloudDataMessageTemplateLookupRepository :
    MongoRepository<CloudDataMessageTemplateLookup, UUID> {
    fun findByCloudDataMessageTemplateLookupIdOrCloudDataMessageAction(
        cloudDataMessageTemplateLookupId: UUID,
        cloudDataMessageAction: String
    ): CloudDataMessageTemplateLookup?

    fun findByCloudDataMessageAction(cloudDataMessageAction: String): CloudDataMessageTemplateLookup?
}