package demo.kotlin.web.dtos

import demo.kotlin.entities.Role
import java.util.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class UserSaveDto(
        @field:NotEmpty
        @field:Size(min = 2, max = 200)
        val username: String?,

        @field:NotEmpty
        @field:Size(min = 8, max = 200)
        val password: String?
) : IDto

data class UserGetDto(
        val username: String,
        val authorities: Collection<Role>,
        val enabled: Boolean,
        val id: UUID
) : IDto
