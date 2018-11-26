package demo.kotlin.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class SecurityContextRepository(
        private val authenticationManager: AuthenticationManager
) : ServerSecurityContextRepository {

    override fun save(swe: ServerWebExchange, sc: SecurityContext) = throw UnsupportedOperationException("Not supported yet.")

    override fun load(swe: ServerWebExchange): Mono<SecurityContext> {
        val request = swe.request
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val authToken = authHeader.substring(7)
            val auth = UsernamePasswordAuthenticationToken(authToken, authToken)
            return this.authenticationManager.authenticate(auth).map { authentication -> SecurityContextImpl(authentication) }
        } else {
            return Mono.empty()
        }
    }
}
