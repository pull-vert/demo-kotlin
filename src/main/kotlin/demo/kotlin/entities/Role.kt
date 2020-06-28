package demo.kotlin.entities

import org.springframework.security.core.GrantedAuthority
import java.util.*

enum class Role(override val id: UUID) : GrantedAuthority, Entity {
    ROLE_USER(UUID.randomUUID()), // fixme : use fixed UUID
    ROLE_ADMIN(UUID.randomUUID());

    override fun getAuthority() = this.name
}
