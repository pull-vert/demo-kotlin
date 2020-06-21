package demo.kotlin.entities

import org.springframework.security.core.GrantedAuthority
import java.util.*

enum class Role(private val id: UUID) : GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    override fun getAuthority() = this.name
}
