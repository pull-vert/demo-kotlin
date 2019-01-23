package demo.kotlin.web.dtos

import java.time.LocalDateTime

data class CowSaveDto(
        val name: String,
        val lastCalvingDate: LocalDateTime?
) : IDto

data class CowGetDto(
        val name: String,
        val lastCalvingDate: LocalDateTime?,
        val id: String
) : IDto
