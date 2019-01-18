package demo.kotlin.repositories

import demo.kotlin.model.entities.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : ReactiveMongoRepository<User, UUID>, ReactiveUserDetailsService
