package demo.kotlin.entities

import org.springframework.data.annotation.Id
import java.util.*

interface IEntity {
    val id: UUID
        @Id get
}
