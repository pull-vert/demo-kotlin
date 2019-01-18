package demo.kotlin.web

import demo.kotlin.dto.AuthRequest
import demo.kotlin.service.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.body

@Component
class AuthenticationHandler(
        private val userService: UserService
) {
    fun auth(req: ServerRequest) =
            req.bodyToMono<AuthRequest>()
                    .flatMap { authRequest -> ok().body(userService.auth(authRequest)) }
}
