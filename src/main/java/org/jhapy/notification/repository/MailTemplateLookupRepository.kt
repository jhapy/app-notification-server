package org.jhapy.notification.repository

import org.jhapy.notification.domain.MailTemplateLookup
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface MailTemplateLookupRepository : MongoRepository<MailTemplateLookup, UUID> {
    fun findByMailTemplateLookupIdOrMailAction(mailTemplateLookupId: UUID, mailAction: String): MailTemplateLookup?
    fun findByMailAction(mailAction: String): MailTemplateLookup?
}