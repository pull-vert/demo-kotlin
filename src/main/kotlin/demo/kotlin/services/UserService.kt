package demo.kotlin.services

import demo.kotlin.entities.User
import demo.kotlin.repositories.*
import demo.kotlin.security.JWTUtil
import demo.kotlin.web.UnauthorizedStatusException
import demo.kotlin.web.dtos.AuthRequestDto
import demo.kotlin.web.dtos.AuthResponseDto
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.ufoss.kolog.Logger
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux

private val logger = Logger.of<UserService>()

@Service
class UserService(
        override val repository: UserRepository,
        private val roleRepository: RoleRepository,
        private val userRoleRepository: UserRoleRepository,
        private val jwtUtil: JWTUtil,
        private val passwordEncoder: PasswordEncoder
) : IService<User>, ReactiveUserDetailsService {

    override fun save(entity: User): Mono<Void> {
        entity.password = passwordEncoder.encode(entity.password)
        return repository.save(entity)
    }

    fun auth(authRequestDto: AuthRequestDto) =
            findByUsername(authRequestDto.username)
                    .map { user ->
                        if (passwordEncoder.matches(authRequestDto.password, user.password)) {
                            AuthResponseDto(jwtUtil.generateToken(user))
                        } else {
                            println("incorrect password ${authRequestDto.password} ${user.password}")
                            throw UnauthorizedStatusException()
                        }
                    }.switchIfEmpty { Mono.error(UnauthorizedStatusException()) }

    fun findAuthenticatedUserById(id: String) =
            repository.findById(id.toUuid())
                    .flatMap(::authenticatedUserByUser)

    override fun findByUsername(username: String?): Mono<UserDetails> =
            Mono.justOrEmpty(username)
                    .flatMap { usernameNotNull -> repository.findByUsername(usernameNotNull) }
                    .flatMap(::authenticatedUserByUser)

    fun init() =
            arrayOf(
                    User("Fred", "password", id = USER_FRED_UUID, enabled = true),
                    User("Boss", "secured_password", id = USER_BOSS_UUID, enabled = true),
                    User("to_delete", "to_delete", id = USER_TO_DELETE_UUID)
            )
                    .toFlux()
                    .doOnNext { user -> logger.info { "saving user $user" } }
                    .flatMap { user -> save(user) }
                    .then()

    private fun authenticatedUserByUser(user: User) =
            userRoleRepository.findRoleIdsByUserId(user.id)
                    .flatMap { roleId -> roleRepository.findById(roleId) }
                    .collectList()
                    .map { roles -> AuthenticatedUser(user.username, user.password, roles, user.enabled, user.id) }
}
