package demo.kotlin.services

import demo.kotlin.entities.Role
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class AuthenticatedUser(
        private var username: String,
        private var password: String,
        private var authorities: MutableCollection<Role>,
        private var enabled: Boolean = true,
        val id: UUID = UUID.randomUUID()
) : UserDetails {

    // UserDetails functions
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

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }
}