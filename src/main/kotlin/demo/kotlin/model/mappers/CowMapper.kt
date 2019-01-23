package demo.kotlin.model.mappers

import demo.kotlin.model.dtos.CowDto
import demo.kotlin.model.entities.Cow

fun CowDto.toCow() = Cow(name, lastCalvingDate)

fun Cow.toCowDto() = CowDto(name, lastCalvingDate)
