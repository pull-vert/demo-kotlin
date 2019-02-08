package demo.kotlin.entities

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.*

@Document
data class Cow(
        val name: String,
        val lastCalvingDate: LocalDateTime? = null,
        private val id: String = UUID.randomUUID().toString()
) : Entity() {
    // Persistable function
    @Id
    override fun getId() = id
}
