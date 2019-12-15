package demo.kotlin.services

import demo.kotlin.USER_FRED_UUID
import demo.kotlin.entities.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.kotlin.test.test

@ExtendWith(SpringExtension::class)
@SpringBootTest
class UserServiceTest(
        @Autowired private val userService: UserService,
        @Autowired private val passwordEncoder: PasswordEncoder
) {

    @Test
    fun `Verify save works and encodes password correctly`() {
        val name = "Bob"
        val rawPassword = "pass"
        userService.save(User(name, rawPassword))
                .test()
                .assertNext { user ->
                    assertThat(user.username).isEqualTo(name)
                    assertThat(user.password)
                            .isNotEqualTo(rawPassword)
                            .matches { password -> passwordEncoder.matches(rawPassword, password) }
                    assertThat(user.isEnabled).isFalse()
                }.verifyComplete()
    }

    @Test
    fun `Verify deleteById is working`() {
        userService.deleteById(USER_FRED_UUID)
                .test()
                .verifyComplete()
    }
}
