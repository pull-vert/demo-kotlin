package demo.kotlin.model.entities

import java.time.LocalDateTime
import java.util.*

data class Cow(
        val name: String,
        val lastCalvingDate: LocalDateTime? = null,
        override val id: UUID = UUID.randomUUID()
) : IEntity
