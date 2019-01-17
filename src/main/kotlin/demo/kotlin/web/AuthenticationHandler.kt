package demo.kotlin.web

import demo.kotlin.dto.AuthRequest
import demo.kotlin.dto.AuthResponse
import demo.kotlin.repository.UserRepository
import demo.kotlin.security.JWTUtil
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.switchIfEmpty

@Component
class AuthenticationHandler(
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder,
        private val userRepository: UserRepository
) : ApiHandler() {
    fun auth(req: ServerRequest) =
            req.bodyToMono<AuthRequest>()
                    .flatMap { authRequest ->
                        userRepository.findByUsername(authRequest.username)
                                .flatMap { user ->
                                    if (passwordEncoder.matches(authRequest.password, user.getPassword())) {
                                        ServerResponse.ok().syncBody(AuthResponse(jwtUtil.generateToken(user)))
                                    } else {
                                        ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
                                    }
                                }
                    }
                    // if no user is found by userRepository.findByUsername
                    .switchIfEmpty { ServerResponse.status(HttpStatus.UNAUTHORIZED).build() }
}
