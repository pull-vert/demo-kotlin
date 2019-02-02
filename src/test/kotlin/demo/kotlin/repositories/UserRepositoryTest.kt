package demo.kotlin.repositories

import demo.kotlin.entities.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest
class UserRepositoryTest(
        @Autowired private val userRepository: UserRepository,
        @Autowired private val passwordEncoder: PasswordEncoder) {

    @Test
    fun `Verify findByUsername returns existing Fred User`() {
        userRepository.findByUsername("Fred")
                .test()
                .consumeNextWith { user ->
                    assertThat(user.username).isEqualTo("Fred")
                    assertThat(passwordEncoder.matches("password", user.password)).isTrue()
                    assertThat((user as User).id).isNotNull()
                }.verifyComplete()
    }

    @Test
    fun `Verify findByUsername returns no John User`() {
        userRepository.findByUsername("John")
                .test()
                .verifyComplete()
    }
}
