package org.jhapy.notification.domain

import org.javers.core.metamodel.annotation.DiffIgnore
import org.springframework.data.annotation.*
import java.io.Serializable
import java.time.Instant
import java.util.*

/**
 * @author jHapy Lead Dev.
 * @version 1.0
 * @since 2019-03-06
 */
abstract class BaseEntity(givenId: UUID? = null) : Serializable {
    /** DB Generated ID  */
    @Id
    private var id: UUID = givenId ?: UUID.randomUUID()

    @Transient
    var persisted: Boolean = givenId != null

    var clientId: UUID? = null

    /** Who create this record (no ID, use username)  */
    @DiffIgnore
    @CreatedBy
    var createdBy: String? = null

    /** When this record has been created  */
    @DiffIgnore
    @CreatedDate
    var created: Instant? = null

    /** How did the last modification of this record (no ID, use username)  */
    @DiffIgnore
    @LastModifiedBy
    var modifiedBy: String? = null

    /** When this record was last updated  */
    @DiffIgnore
    @LastModifiedDate
    var modified: Instant? = null

    /** Version of the record. Used for synchronization and concurrent access.  */
    @DiffIgnore
    @Version
    var version: Long? = null

    /** Indicate if the current record is active (deactivate instead of delete)  */
    var isActive = true

    fun getId(): UUID = id

    fun setId(id: UUID) {
        this.id = id
    }

    fun isNew(): Boolean = !persisted

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): kotlin.Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is BaseEntity -> false
            else -> getId() == other.getId()
        }
    }
}