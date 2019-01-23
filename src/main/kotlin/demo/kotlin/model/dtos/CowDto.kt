package demo.kotlin.model.dtos

import java.time.LocalDateTime

data class CowDto(val name: String,
                  val lastCalvingDate: LocalDateTime?) : IDto