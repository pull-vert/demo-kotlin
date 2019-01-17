package demo.kotlin.web

import demo.kotlin.repository.UserRepository
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import reactor.core.publisher.toMono

@Component
class UserHandler(private val userRepository: UserRepository) : ApiHandler() {

    fun delete(req: ServerRequest) =
        req.pathVariable("userId").toMono()
                .map { it.toUuid() } // throws ResponseStatusException
                .flatMap { userRepository.deleteById(it) }
                .then(ok().build())
                .switchIfEmpty(notFound().build())
}