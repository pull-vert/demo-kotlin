package demo.kotlin.entities

import java.util.*

data class User(
        var username: String,
        var password: String,
        var enabled: Boolean = false,
        override val id: UUID = UUID.randomUUID()
) : Entity
