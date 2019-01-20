package demo.kotlin.model.entities

import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class User(
        private var username: String,
        private var password: String,
        private var authorities: MutableList<Role> = mutableListOf(Role.ROLE_USER), // Default Role : USER
        var active: Boolean = true,
        override val id: UUID = UUID.randomUUID()
) : IEntity, UserDetails {

    // UserDetails
    override fun getUsername() = this.username

    override fun getPassword() = this.password

    override fun getAuthorities() = this.authorities

    override fun isEnabled() = this.active

    override fun isCredentialsNonExpired() = false

    override fun isAccountNonExpired() = false

    override fun isAccountNonLocked() = false

    fun setPassword(password: String) {
        this.password = password
    }
}
