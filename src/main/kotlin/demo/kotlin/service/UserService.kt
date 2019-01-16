package demo.kotlin.service

import demo.kotlin.model.User
import demo.kotlin.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class UserService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder
) {
    fun save(user: User): Mono<User> {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    fun deleteById(id: UUID) = userRepository.deleteById(id)
}
