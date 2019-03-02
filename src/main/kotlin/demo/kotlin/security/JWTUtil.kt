package demo.kotlin.security

import io.jsonwebtoken.*
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

private val logger = KotlinLogging.logger {}

@Component
class JWTUtil(
        @Value("\${jwt.secret}") private val secret: String,
        @Value("\${jwt.expiration}") private val expirationTime: Long //in second
) {

    private val jwtParser = Jwts.parser().setSigningKey(secret.toByteArray())

    fun getAllClaimsFromToken(token: String) = jwtParser.parseClaimsJws(token).body

    fun getUsernameFromToken(token: String) = getAllClaimsFromToken(token).subject

    fun generateToken(user: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        claims["authorities"] = user.authorities
        claims["enabled"] = user.isEnabled
        return doGenerateToken(user.getUsername(), claims)
    }

    fun validateToken(token: String): Boolean {
        try {
            // token must be signed
            if (!jwtParser.isSigned(token)) return false
            // parse token to get claims, will throw ExpiredJwtException if expired at current time
            val claims = getAllClaimsFromToken(token)
            val enabled = claims.getOrDefault("enabled", false) as Boolean
            if (!enabled) {
                logger.error("Invalid JWT, User ${getUsernameFromToken(token)} is inactive")
            }
            return enabled
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
            return false
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
            return false
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
            return false
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
            return false
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
            return false
        }
    }

    private fun doGenerateToken(username: String, claims: Map<String, Any>): String {
        val createdDate = Date()
        val expirationDate = Date(createdDate.getTime() + expirationTime * 1000)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret.toByteArray())
                .compact()
    }
}
