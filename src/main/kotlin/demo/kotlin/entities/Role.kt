package demo.kotlin.entities

import org.springframework.security.core.GrantedAuthority
import java.util.*

enum class Role(override val id: UUID = UUID.randomUUID()) : GrantedAuthority, Entity {
    ROLE_USER, // fixme : use fixed UUID
    ROLE_ADMIN;

    override fun getAuthority() = this.name
}
