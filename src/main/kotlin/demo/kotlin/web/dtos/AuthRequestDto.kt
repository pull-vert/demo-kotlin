package demo.kotlin.web.dtos

data class AuthRequestDto(
        val username: String,
        val password: String
) : IDto
