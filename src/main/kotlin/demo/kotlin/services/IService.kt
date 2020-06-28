package demo.kotlin.services

import demo.kotlin.entities.Entity
import demo.kotlin.repositories.Repo
import demo.kotlin.web.NotFoundStatusException
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import java.util.*

interface IService<T : Entity> {

    val repository: Repo<T>

    fun findById(id: String) =
            id.toMono()
                    .map { uuid -> UUID.fromString(uuid) }
                    .flatMap { uuid -> repository.findById(uuid) }
                    .switchIfEmpty { throw NotFoundStatusException() }

    fun findAll() = repository.findAll()

    fun save(entity: T) = repository.save(entity)

    fun deleteById(id: String) =
            id.toMono()
                    .map { uuid -> UUID.fromString(uuid) }
                    .flatMap { uuid -> repository.deleteById(uuid) }
                    .then()

    fun deleteAll() = repository.deleteAll()
}
