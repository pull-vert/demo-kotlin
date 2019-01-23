package demo.kotlin.model

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import demo.kotlin.USER_FRED_UUID
import demo.kotlin.entities.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.util.ResourceUtils
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
class JacksonTest(@Autowired private val objectMapper: ObjectMapper) {

    @Test
    fun `Verify simple User serialize works`() {
        val fred = User("Fred", "password", id = UUID.fromString(USER_FRED_UUID))
        println(objectMapper.writeValueAsString(fred))
    }

    @Test
    fun `Verify simple User deserialize works`() {
        val file = ResourceUtils.getFile("classpath:model/user.json")
        val user = objectMapper.readValue<User>(file)
        assertThat(user.username).isEqualTo("Fred")
        assertThat(user.id).isEqualTo(UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f"))
    }
}