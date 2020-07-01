package demo.kotlin.repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.kotlin.test.test

@SpringBootTest
class UserRepositoryTest(@Autowired private val userRepository: UserRepository) {

    @Test
    fun `Verify findByUsername returns existing Fred User`() {
        userRepository.findByUsername("Fred")
                .test()
                .consumeNextWith { user ->
                    assertThat(user.username).isEqualTo("Fred")
                    assertThat(user.password).isNotNull()
                    assertThat(user.id).isNotNull()
                }.verifyComplete()
    }

    @Test
    fun `Verify findByUsername returns no John User`() {
        userRepository.findByUsername("John")
                .test()
                .verifyComplete()
    }
}
