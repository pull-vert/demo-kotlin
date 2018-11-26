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

@Component
class AuthenticationHandler(
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder,
        private val userRepository: UserRepository
) {
    fun auth(req: ServerRequest)=
            req.bodyToMono<AuthRequest>()
            .flatMap { ar -> userRepository.findByUsername(ar.username).map { ud -> Pair(ud, ar.password) } }
                    .flatMap { pair -> if (passwordEncoder.matches(pair.second, pair.first.getPassword())) {
                                ServerResponse.ok().syncBody(AuthResponse(jwtUtil.generateToken(pair.first)))
                            } else {
                                ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
                            }
                        }
}