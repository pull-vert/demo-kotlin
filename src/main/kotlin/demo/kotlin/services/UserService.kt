package demo.kotlin.services

import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import demo.kotlin.entities.User
import demo.kotlin.repositories.UserRepository
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.UnauthorizedStatusException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.switchIfEmpty
import reactor.core.publisher.toMono

@Service
class UserService(
        override val repository: UserRepository,
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder
) : IService<User> {

    override fun save(entity: User) =
            entity.toMono()
                    .doOnNext { it.password = passwordEncoder.encode(it.password) }
                    .flatMap { repository.save(it) }

    fun auth(authRequestDto: AuthRequestDto) =
            repository.findByUsername(authRequestDto.username)
                    .map { user ->
                        if (passwordEncoder.matches(authRequestDto.password, user.getPassword())) {
                            AuthResponseDto(jwtUtil.generateToken(user))
                        } else {
                            throw UnauthorizedStatusException()
                        }
                    }.switchIfEmpty { throw UnauthorizedStatusException() }
}
