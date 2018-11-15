package demo.kotlin.web

import demo.kotlin.repository.CowRepository
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

internal class CowHandler(private val cowRepository: CowRepository) {

    fun findByName(req: ServerRequest)= ok().body(cowRepository.findByName(req.pathVariable("name")))

    fun findAll(req: ServerRequest)= ok().body(cowRepository.findAll())
}
