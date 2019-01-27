package demo.kotlin.entities

import org.springframework.security.core.userdetails.UserDetails
import java.util.*

class User(
        private var username: String,
        private var password: String,
        private var authorities: MutableList<Role> = mutableListOf(Role.ROLE_USER), // Default Role : USER
        private var enabled: Boolean = false,
        private val id: UUID = UUID.randomUUID()
) : Entity(), UserDetails {

    override fun getId() = id

    // UserDetails
    override fun getUsername() = this.username

    override fun getPassword() = this.password

    override fun getAuthorities() = this.authorities

    override fun isEnabled() = this.enabled

    override fun isCredentialsNonExpired() = true

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true

    fun setPassword(password: String) {
        this.password = password
    }
}
