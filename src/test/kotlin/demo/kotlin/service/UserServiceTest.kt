package demo.kotlin.service

import demo.kotlin.USER_FRED_UUID
import demo.kotlin.model.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.test
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class UserServiceTest(
        @Autowired private val userService: UserService,
        @Autowired private val passwordEncoder: PasswordEncoder) {

    @Test
    fun `Verify save works and encodes password correcly`() {
        val name = "Bob"
        val rawPassword = "pass"
        val user = User(name, rawPassword)
        userService.save(user)
                .test()
                .assertNext {
                    assertThat(it.username).isEqualTo(name)
                    assertThat(it.password)
                            .isNotEqualTo(rawPassword)
                            .satisfies {
                                assertThat(passwordEncoder.matches(rawPassword, it)).isTrue()
                            }
                }.verifyComplete()
    }

    @Test
    fun `Verify deleteById is working`() {
        userService.deleteById(UUID.fromString(USER_FRED_UUID))
                .test()
                .verifyComplete()
    }
}