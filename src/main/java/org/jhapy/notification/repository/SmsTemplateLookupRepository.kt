package org.jhapy.notification.repository

import org.jhapy.notification.domain.SmsTemplateLookup
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface SmsTemplateLookupRepository : MongoRepository<SmsTemplateLookup, UUID> {
    fun findBySmsTemplateLookupIdOrSmsAction(smsTemplateLookupId: UUID, smsAction: String): SmsTemplateLookup?
    fun findBySmsAction(smsAction: String): SmsTemplateLookup?
}