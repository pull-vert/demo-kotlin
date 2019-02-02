package demo.kotlin.repositories

import demo.kotlin.entities.Cow
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface CowRepository : IRepository<Cow> {
    fun findByName(name: String): Mono<Cow>
}
