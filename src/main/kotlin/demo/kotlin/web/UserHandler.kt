package demo.kotlin.web

import demo.kotlin.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body

@Component
class UserHandler(private val userService: UserService) {

    // todo add save handler
    fun delete(req: ServerRequest) = ok().body(userService.deleteById(req.pathVariable("userId")))
}
