package demo.kotlin.web

import demo.kotlin.repository.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono
import java.util.*

@Component
class UserHandler(private val userRepository: UserRepository) {

    fun delete(req: ServerRequest): Mono<ServerResponse> =
            ok().body(userRepository.deleteById(UUID.fromString(req.pathVariable("userId"))))
}