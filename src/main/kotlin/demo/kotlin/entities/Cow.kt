package demo.kotlin.entities

import org.springframework.data.annotation.Id
import java.time.LocalDateTime
import java.util.*

data class Cow(
        val name: String,
        val lastCalvingDate: LocalDateTime? = null,
        private val id: UUID = UUID.randomUUID()
) : Entity() {
    // Persistable function
    @Id
    override fun getId() = id
}
