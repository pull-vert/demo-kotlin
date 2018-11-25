package demo.kotlin.model

import org.springframework.security.core.GrantedAuthority

enum class Role : GrantedAuthority {
    USER,
    ADMIN;

    override fun getAuthority() = this.name
}