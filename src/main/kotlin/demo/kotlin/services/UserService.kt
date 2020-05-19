package demo.kotlin.services

import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import demo.kotlin.entities.User
import demo.kotlin.repositories.UserDeleteRepository
import demo.kotlin.repositories.UserRepository
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.UnauthorizedStatusException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono

@Service
class UserService(
        override val repository: UserRepository,
        private val userDeleteRepository: UserDeleteRepository,
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder
) : IService<User> {

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

    override fun deleteById(id: String) = userDeleteRepository.deleteById(id)
}
