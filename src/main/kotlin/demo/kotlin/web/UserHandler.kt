package demo.kotlin.web

import demo.kotlin.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.noContent

@Component
class UserHandler(private val userService: UserService) {

    // todo add GET by ID and save handlers, save returns HTTP 201 created with URI to GET by ID
    fun delete(req: ServerRequest) = noContent().build(userService.deleteById(req.pathVariable("userId")))
}
