package demo.kotlin.web.dtos

import java.time.LocalDate
import java.util.*
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size

data class CowSaveDto(
        @field:NotEmpty
        @field:Size(min = 2, max = 50)
        val name: String?,
        val lastCalvingDate: LocalDate?
) : IDto

data class CowGetDto(
        val name: String,
        val lastCalvingDate: LocalDate?,
        val id: UUID
) : IDto
