package demo.kotlin.entities

import org.springframework.data.annotation.Id
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

data class User(
        private var username: String,
        private var password: String,
        private var authorities: MutableList<Role> = mutableListOf(Role.ROLE_USER), // Default Role : USER
        private var enabled: Boolean = false,
        private val id: UUID = UUID.randomUUID()
) : Entity(), UserDetails {

    // Persistable function
    @Id
    override fun getId() = id

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
