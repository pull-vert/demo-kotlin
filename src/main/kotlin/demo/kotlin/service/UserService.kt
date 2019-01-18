package demo.kotlin.service

import demo.kotlin.dto.AuthRequest
import demo.kotlin.dto.AuthResponse
import demo.kotlin.model.User
import demo.kotlin.repository.UserRepository
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.NotFoundStatusException
import demo.kotlin.web.UnauthorizedStatusException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.switchIfEmpty

@Service
class UserService(
        private val userRepository: UserRepository,
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder
) : IService {

    fun save(user: User): Mono<User> {
        user.password = passwordEncoder.encode(user.password)
        return userRepository.save(user)
    }

    fun deleteById(userId: String) =
            userRepository.findById(userId.toUuid())
                    .switchIfEmpty { throw NotFoundStatusException() }
                    .flatMap(userRepository::delete)

    fun auth(authRequest: AuthRequest) =
            userRepository.findByUsername(authRequest.username)
                    .map { user ->
                        if (passwordEncoder.matches(authRequest.password, user.getPassword())) {
                            AuthResponse(jwtUtil.generateToken(user))
                        } else {
                            throw UnauthorizedStatusException()
                        }
                    }.switchIfEmpty { throw UnauthorizedStatusException() }
}
