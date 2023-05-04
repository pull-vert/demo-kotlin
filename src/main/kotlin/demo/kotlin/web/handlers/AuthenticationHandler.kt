package demo.kotlin.web.handlers

import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.services.UserService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.bodyToMono
import org.springframework.web.reactive.function.server.body
import jakarta.validation.Validator

@Component
class AuthenticationHandler(
        private val userService: UserService,
        override val validator: Validator
) : Validate {

    fun auth(req: ServerRequest) =
            req.bodyToMono<AuthRequestDto>()
                    .doOnNext(::callValidator)
                    .flatMap { authRequest -> ok().body(userService.auth(authRequest)) }
}
