package demo.kotlin

import demo.kotlin.entities.Cow
import demo.kotlin.entities.User
import demo.kotlin.repositories.CowRepository
import demo.kotlin.services.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.toFlux
import java.time.LocalDateTime
import java.util.*

internal const val COW_MARGUERITE_UUID = "e48ccc7e-c1b8-41b8-91f5-ab5528ab292b"
internal const val USER_FRED_UUID = "79e9eb45-2835-49c8-ad3b-c951b591bc7f"
internal const val USER_BOSS_UUID = "67d4306e-d99d-4e54-8b1d-5b1e92691a4e"

@Component
class DatabaseInitializer(
        private val cowRepository: CowRepository,
        private val userService: UserService
) : CommandLineRunner {
    override fun run(vararg args: String) {

        // uncomment if targetting a real MongoDB Database (not embedded)
//        cowRepository.deleteAll()
//                .block()
//        userRepository.deleteAll()
//                .block()
        val marguerite = Cow("Marguerite", LocalDateTime.of(2017, 9, 28, 13, 30), id = UUID.fromString(COW_MARGUERITE_UUID))
        val laNoiraude = Cow("La Noiraude")
        listOf(marguerite, laNoiraude)
                .toFlux()
                .flatMap {
                    cowRepository.save(it)
                }.doOnNext { println("saving cow $it") }
                .blockLast()

        val fred = User("Fred", "password", id = UUID.fromString(USER_FRED_UUID), enabled = true)
        val boss = User("Boss", "secured_password", id = UUID.fromString(USER_BOSS_UUID), enabled = true)
        listOf(fred, boss)
                .toFlux()
                .flatMap {
                    userService.save(it)
                }.doOnNext { println("saving user $it, entity informations; createdBy=${it.createdBy}, createdDate=${it.createdDate}") }
                .blockLast()
    }
}
