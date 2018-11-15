package demo.kotlin

import demo.kotlin.model.Cow
import demo.kotlin.repository.CowRepository
import reactor.core.publisher.toFlux
import java.time.LocalDateTime

internal class DatabaseInitializer(
        private val cowRepository: CowRepository
) {
    fun init() {
        val marguerite = Cow("Marguerite", LocalDateTime.of(2017, 9, 28, 13, 30))
        val laNoiraude = Cow("La Noiraude")

        // uncomment if targetting a real MongoDB Database (not embedded)
//        cowRepository.deleteAll()
//                .block()

        listOf(marguerite, laNoiraude)
                .toFlux()
                .flatMap {
                    println("saving ${it.name}")
                    cowRepository.save(it)
                }.blockLast()
    }
}
