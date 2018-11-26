package demo.kotlin.repository

import demo.kotlin.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : ReactiveMongoRepository<User, UUID>, ReactiveUserDetailsService
