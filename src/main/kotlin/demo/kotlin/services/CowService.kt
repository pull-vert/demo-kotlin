package demo.kotlin.services

import demo.kotlin.entities.Cow
import demo.kotlin.repositories.COW
import demo.kotlin.repositories.CowRepository
import demo.kotlin.web.NotFoundStatusException
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class CowService(override val repository: CowRepository) : IService<Cow, COW> {

    fun findByName(name: String) =
            repository.findByName(name)
                    .switchIfEmpty { Mono.error(NotFoundStatusException("No Cow found for $name name")) }
}
