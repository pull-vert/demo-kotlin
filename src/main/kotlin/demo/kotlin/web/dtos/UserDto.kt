package demo.kotlin.web.dtos

import demo.kotlin.entities.Role

data class UserSaveDto(
        val username: String,
        val password: String
) : IDto

data class UserGetDto(
        val username: String,
        val authorities: List<Role>,
        val enabled: Boolean,
        val id: String
) : IDto
