package demo.kotlin.web.dtos

import javax.validation.constraints.NotEmpty

data class AuthRequestDto(
        @field:NotEmpty
        val username: String?,

        @field:NotEmpty
        val password: String?
) : IDto
