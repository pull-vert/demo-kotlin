package demo.kotlin.services

import demo.kotlin.entities.Entity
import demo.kotlin.repositories.Repo
import demo.kotlin.web.NotFoundStatusException
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

interface IService<T : Entity> {

    val repository: Repo<T>

    fun findById(id: UUID) =
            repository.findById(id)
                    .switchIfEmpty { throw NotFoundStatusException() }

    fun findAll() = repository.findAll()

    fun save(entity: T) = repository.save(entity)

    fun deleteById(id: UUID) = repository.deleteById(id)

    fun deleteAll() = repository.deleteAll()
}
