package demo.kotlin.repositories

import demo.kotlin.entities.User
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : IRepository<User>, ReactiveUserDetailsService
