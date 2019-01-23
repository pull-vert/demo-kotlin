package demo.kotlin.web.handlers

import demo.kotlin.model.dtos.CowDto
import demo.kotlin.model.entities.Cow
import demo.kotlin.model.mappers.toCowDto
import demo.kotlin.services.CowService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@Component
class CowHandler(override val service: CowService): IHandler<Cow, CowDto> {

    override val entityToDto: (Cow) -> CowDto = { it.toCowDto() }

    fun findByName(req: ServerRequest)= ok().body(service.findByName(req.pathVariable("name")).map { cow -> cow.toCowDto() })
}
