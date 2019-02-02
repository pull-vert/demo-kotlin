package demo.kotlin.web.dtos

import java.time.LocalDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class CowSaveDto(
        @field:NotEmpty
        @field:Size(min = 2, max = 50)
        val name: String?,
        val lastCalvingDate: LocalDateTime?
) : IDto

data class CowGetDto(
        val name: String,
        val lastCalvingDate: LocalDateTime?,
        val id: String
) : IDto
