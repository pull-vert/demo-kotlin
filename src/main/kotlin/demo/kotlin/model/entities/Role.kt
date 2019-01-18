package demo.kotlin.model.entities

import org.springframework.security.core.GrantedAuthority

enum class Role : GrantedAuthority {
    ROLE_USER,
    ROLE_ADMIN;

    override fun getAuthority() = this.name
}
