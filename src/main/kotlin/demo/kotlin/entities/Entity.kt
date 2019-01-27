package demo.kotlin.entities

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import java.time.LocalDateTime
import java.util.*

abstract class Entity(
        @CreatedDate var createdDate: LocalDateTime? = null,
        @LastModifiedDate var lastModifiedDate: LocalDateTime? = null) : Persistable<UUID> {
    // Persistable functions
    override fun isNew() = (null == createdDate)
}
