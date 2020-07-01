package demo.kotlin.web.dtos

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import demo.kotlin.entities.Role.Companion.ROLE_USER
import demo.kotlin.repositories.USER_FRED_UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.util.ResourceUtils

@SpringBootTest
class JacksonTest(@Autowired private val objectMapper: ObjectMapper) {

    @Test
    fun `Verify User serialize works`() {
        val user = UserGetDto("Fred", listOf(ROLE_USER), true, USER_FRED_UUID)
        val json = objectMapper.writeValueAsString(user)
        assertThat(json).isEqualTo("{\"username\":\"Fred\",\"authorities\":[\"ROLE_USER\"],\"enabled\":true,\"id\":\"79e9eb45-2835-49c8-ad3b-c951b591bc7f\"}")
    }

    @Test
    fun `Verify User deserialize all fields works`() {
        val file = ResourceUtils.getFile("classpath:web/dtos/user.json")
        val user = objectMapper.readValue<UserSaveDto>(file)
        assertThat(user.username).isEqualTo("Fred")
        assertThat(user.password).isEqualTo("password")
    }

    @Test
    fun `Verify User deserialize no password works`() {
        val file = ResourceUtils.getFile("classpath:web/dtos/user_no_password.json")
        val user = objectMapper.readValue<UserSaveDto>(file)
        assertThat(user.username).isEqualTo("Fred")
        assertThat(user.password).isNull()
    }
}
