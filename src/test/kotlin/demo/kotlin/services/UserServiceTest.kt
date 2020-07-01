package demo.kotlin.services

import demo.kotlin.entities.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

@SpringBootTest
class UserServiceTest(
        @Autowired private val userService: UserService,
        @Autowired private val passwordEncoder: PasswordEncoder
) {

    @Test
    fun `Verify save works and encodes password correctly`() {
        val name = "Bob"
        val rawPassword = "pass"
        User(name, rawPassword)
                .toMono()
                .doOnNext { user -> userService.save(user) }
                .test()
                .assertNext { user ->
                    assertThat(user.username).isEqualTo(name)
                    assertThat(user.password)
                            .isNotEqualTo(rawPassword)
                            .matches { password -> passwordEncoder.matches(rawPassword, password) }
                    assertThat(user.enabled).isFalse()
                }.verifyComplete()
    }

    @Test
    fun `Verify findByUsername returns existing Fred User`() {
        userService.findByUsername("Fred")
                .test()
                .consumeNextWith { user ->
                    assertThat(user.username).isEqualTo("Fred")
                    assertThat(passwordEncoder.matches("password", user.password)).isTrue()
                    assertThat((user as AuthenticatedUser).id).isNotNull()
                }.verifyComplete()
    }

    @Test
    fun `Verify findByUsername returns no John User`() {
        userService.findByUsername("John")
                .test()
                .verifyComplete()
    }
}
