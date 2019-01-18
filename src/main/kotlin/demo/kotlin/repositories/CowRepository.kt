package demo.kotlin.repositories

import demo.kotlin.model.entities.Cow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface CowRepository : ReactiveMongoRepository<Cow, UUID> {
    fun findByName(name: String): Mono<Cow>
}
