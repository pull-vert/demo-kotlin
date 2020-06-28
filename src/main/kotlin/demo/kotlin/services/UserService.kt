package demo.kotlin.services

import demo.kotlin.entities.User
import demo.kotlin.repositories.RoleRepository
import demo.kotlin.repositories.UserRepository
import demo.kotlin.repositories.UserRoleRepository
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.UnauthorizedStatusException
import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class UserService(
        override val repository: UserRepository,
        private val roleRepository: RoleRepository,
        private val userRoleRepository: UserRoleRepository,
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder
) : IService<User>, ReactiveUserDetailsService {

    override fun save(user: User): Mono<Void> {
        user.password = passwordEncoder.encode(user.password)
        return repository.save(user)
    }

    fun auth(authRequestDto: AuthRequestDto) =
            findByUsername(authRequestDto.username)
                    .map { user ->
                        if (passwordEncoder.matches(authRequestDto.password, user.password)) {
                            AuthResponseDto(jwtUtil.generateToken(user))
                        } else {
                            throw UnauthorizedStatusException()
                        }
                    }.switchIfEmpty { Mono.error(UnauthorizedStatusException()) }

    fun findAuthenticatedUserById(id: String) =
            id.toMono()
                    .map { uuid -> UUID.fromString(uuid) }
                    .flatMap { uuid -> repository.findById(uuid) }
                    .flatMap(::authenticatedUserByUser)

    override fun findByUsername(username: String?): Mono<UserDetails> =
            Mono.justOrEmpty(username)
                    .flatMap { usernameNotNull -> repository.findByUsername(usernameNotNull) }
                    .flatMap(::authenticatedUserByUser)

    private fun authenticatedUserByUser(user: User) =
            userRoleRepository.findRoleIdsByUserId(user.id)
                    .flatMap { roleId -> roleRepository.findById(roleId) }
                    .collectList()
                    .map { roles -> AuthenticatedUser(user.username, user.password, roles, user.enabled, user.id) }
}
