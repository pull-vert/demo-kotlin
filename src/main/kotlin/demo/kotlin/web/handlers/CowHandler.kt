package demo.kotlin.web.handlers

import demo.kotlin.entities.Cow
import demo.kotlin.repositories.COW
import demo.kotlin.services.CowService
import demo.kotlin.web.dtos.CowGetDto
import demo.kotlin.web.dtos.CowSaveDto
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import javax.validation.Validator

@Component
class CowHandler(
        override val service: CowService,
        override val validator: Validator
) : IHandler<Cow, COW, CowGetDto, CowSaveDto> {

    override fun entityToGetDto(entity: Cow) = CowGetDto(entity.name, entity.lastCalvingDate, entity.id)

    override fun saveDtoToEntity(saveDto: CowSaveDto) = Cow(saveDto.name!!, saveDto.lastCalvingDate)

    override val findByIdUrl = "/api/cows"

    fun findByName(req: ServerRequest) =
            ok().body(
                    service.findByName(req.pathVariable("name"))
                            .map(::entityToGetDto)
            )
}
