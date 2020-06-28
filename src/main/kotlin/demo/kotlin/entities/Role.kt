package demo.kotlin.entities

import org.springframework.security.core.GrantedAuthority
import java.lang.IllegalArgumentException
import java.util.*

data class Role(
        private val authority: String,
        override val id: UUID = UUID.randomUUID()
) : GrantedAuthority, Entity {
    override fun getAuthority() = this.authority

    companion object {
        @JvmStatic
        val ROLE_USER = Role("user")
        @JvmStatic
        val ROLE_ADMIN = Role("admin")

        @JvmStatic
        fun valueOf(authority: String) =
                when(authority) {
                    "user" -> ROLE_USER
                    "admin" -> ROLE_ADMIN
                    else -> throw IllegalArgumentException("$authority is incorrect")
                }
    }
}
