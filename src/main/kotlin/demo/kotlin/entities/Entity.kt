package demo.kotlin.entities

import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
abstract class Entity(
        @CreatedBy var createdBy: String? = null,
        @CreatedDate var createdDate: LocalDateTime? = null,
        @LastModifiedBy var lastModifiedBy: String? = null,
        @LastModifiedDate var lastModifiedDate: LocalDateTime? = null
) : Persistable<UUID> {
    // Persistable functions
    override fun isNew() = (null == createdDate)

    override fun toString() = "Entity{createdBy=$createdBy, createdDate=$createdDate, lastModifiedBy=$lastModifiedBy, lastModifiedDate=$lastModifiedDate}"
}
