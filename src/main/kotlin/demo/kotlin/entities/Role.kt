package demo.kotlin.entities

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIdentityReference
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.springframework.security.core.GrantedAuthority
import java.lang.IllegalArgumentException
import java.util.*

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "authority")
@JsonIdentityReference(alwaysAsId = true)
data class Role(
        private val authority: String,
        override val id: UUID = UUID.randomUUID()
) : GrantedAuthority, Entity {
    override fun getAuthority() = this.authority

    companion object {
        @JvmStatic
        val ROLE_USER = Role("ROLE_USER")
        @JvmStatic
        val ROLE_ADMIN = Role("ROLE_ADMIN")

        @JvmStatic
        fun valueOf(authority: String) =
                when(authority) {
                    "ROLE_USER" -> ROLE_USER
                    "ROLE_ADMIN" -> ROLE_ADMIN
                    else -> throw IllegalArgumentException("$authority is incorrect")
                }
    }
}
