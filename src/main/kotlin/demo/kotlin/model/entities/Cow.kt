package demo.kotlin.model.entities

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*

data class Cow(
        val name: String,
        val lastCalvingDate: LocalDateTime? = null,
        @Id val id: UUID = UUID.randomUUID()
)
