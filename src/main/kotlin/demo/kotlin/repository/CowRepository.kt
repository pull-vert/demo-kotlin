package demo.kotlin.repository

import demo.kotlin.model.Cow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

@Repository
interface CowRepository : ReactiveMongoRepository<Cow, UUID> {
    fun findByName(name: String): Mono<Cow>
}
