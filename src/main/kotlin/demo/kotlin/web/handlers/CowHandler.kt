package demo.kotlin.web.handlers

import demo.kotlin.services.CowService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@Component
class CowHandler(private val cowService: CowService) {

    fun findByName(req: ServerRequest)= ok().body(cowService.findByName(req.pathVariable("name")))

    fun findAll(req: ServerRequest)= ok().body(cowService.findAll())
}
