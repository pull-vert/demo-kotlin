package demo.kotlin.security

import demo.kotlin.model.Role
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


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
            val auth = UsernamePasswordAuthenticationToken(username, null, roles)
            return Mono.just(auth)
        } else {
            return Mono.empty()
        }
    }
}
