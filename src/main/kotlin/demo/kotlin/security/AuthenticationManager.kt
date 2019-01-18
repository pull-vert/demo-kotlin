package demo.kotlin.security

import demo.kotlin.model.entities.Role
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono


@Component
class AuthenticationManager(
        private val jwtUtil: JWTUtil
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        val authToken = authentication.credentials.toString()

        var username: String?
        try {
            username = jwtUtil.getUsernameFromToken(authToken)
        } catch (e: Exception) {
            username = null
        }

        if (null != username && jwtUtil.validateToken(authToken)) {
            val claims = jwtUtil.getAllClaimsFromToken(authToken)
            val roles = claims.get("roles", List::class.java)
                    .map { Role.valueOf(it as String) }
            return UsernamePasswordAuthenticationToken(username, null, roles).toMono()
        } else {
            return Mono.empty()
        }
    }
}
