package demo.kotlin.entities

import org.springframework.security.core.GrantedAuthority
import java.util.*

enum class Role(internal val id: UUID) : GrantedAuthority {
    ROLE_USER(UUID.randomUUID()), // fixme : use fixed UUID
    ROLE_ADMIN(UUID.randomUUID());

    override fun getAuthority() = this.name
}
