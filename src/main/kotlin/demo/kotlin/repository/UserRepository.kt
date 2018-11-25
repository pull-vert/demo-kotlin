package demo.kotlin.repository

import demo.kotlin.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface UserRepository : ReactiveMongoRepository<User, UUID>, ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails>
}
