package demo.kotlin

import demo.kotlin.model.Cow
import demo.kotlin.model.User
import demo.kotlin.repository.CowRepository
import demo.kotlin.repository.UserRepository
import demo.kotlin.service.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import reactor.core.publisher.toFlux
import java.time.LocalDateTime

@Component
class DatabaseInitializer(
        private val cowRepository: CowRepository,
        private val userService: UserService
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val marguerite = Cow("Marguerite", LocalDateTime.of(2017, 9, 28, 13, 30))
        val laNoiraude = Cow("La Noiraude")

        // uncomment if targetting a real MongoDB Database (not embedded)
//        cowRepository.deleteAll()
//                .block()
//        userRepository.deleteAll()
//                .block()

        listOf(marguerite, laNoiraude)
                .toFlux()
                .flatMap {
                    println("saving ${it.name}")
                    cowRepository.save(it)
                }.blockLast()

        userService.save(User("Fred", "password"))
                .block()
    }
}
