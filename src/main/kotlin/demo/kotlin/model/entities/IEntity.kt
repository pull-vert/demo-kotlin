package demo.kotlin.model.entities

import org.springframework.data.annotation.Id
import java.util.*

interface IEntity {
    val id: UUID
        @Id get
}
