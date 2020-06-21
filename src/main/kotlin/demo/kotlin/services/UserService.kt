package demo.kotlin.services

import demo.kotlin.entities.User
import demo.kotlin.repositories.UserRepository
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.UnauthorizedStatusException
import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toMono

@Service
class UserService(
        override val repository: UserRepository,
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder
) : IService<User>, ReactiveUserDetailsService {

    override fun save(entity: User) =
            entity.toMono()
                    .doOnNext { user -> user.password = passwordEncoder.encode(user.password) }
                    .flatMap { user -> repository.save(user) }

    fun auth(authRequestDto: AuthRequestDto) =
            repository.findByUsername(authRequestDto.username)
                    .map { user ->
                        if (passwordEncoder.matches(authRequestDto.password, user.password)) {
                            AuthResponseDto(jwtUtil.generateToken(user))
                        } else {
                            throw UnauthorizedStatusException()
                        }
                    }.switchIfEmpty { throw UnauthorizedStatusException() }
}
