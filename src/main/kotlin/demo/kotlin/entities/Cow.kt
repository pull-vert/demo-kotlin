package demo.kotlin.entities

import java.time.LocalDate
import java.util.*

data class Cow(
        val name: String,
        val lastCalvingDate: LocalDate? = null,
        override val id: UUID = UUID.randomUUID()
) : Entity
