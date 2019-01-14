package demo.kotlin.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*


@Component
class JWTUtil(
        @Value("\${jwt.secret}") private val secret: String,
        @Value("\${jwt.expiration}") private val expirationTime: String
) {

    fun getAllClaimsFromToken(token: String) = Jwts.parser().setSigningKey(secret.toByteArray()).parseClaimsJws(token).body

    fun getUsernameFromToken(token: String): String = getAllClaimsFromToken(token).subject

    fun getExpirationDateFromToken(token: String) = getAllClaimsFromToken(token).expiration

    fun generateToken(user: UserDetails): String {
        val claims = mutableMapOf<String, Any>()
        claims["roles"] = user.authorities
        claims["enable"] = user.isEnabled
        return doGenerateToken(user.getUsername(), claims)
    }

    fun validateToken(token: String) = !isTokenExpired(token, Date())

    private fun doGenerateToken(username: String, claims: Map<String, Any>): String {
        val expirationTimeLong = expirationTime.toLong() //in second

        val createdDate = Date()
        val expirationDate = Date(createdDate.getTime() + expirationTimeLong * 1000)
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret.toByteArray())
                .compact()
    }

    internal fun isTokenExpired(token: String, date: Date): Boolean {
        val expiration = getExpirationDateFromToken(token)
        return expiration.before(date)
    }
}
