package demo.kotlin.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserRepositoryTest(@Autowired private val userRepository: UserRepository) {
}
