package demo.kotlin.services

import demo.kotlin.entities.Entity
import demo.kotlin.repositories.Repo
import demo.kotlin.web.BadRequestStatusException
import demo.kotlin.web.NotFoundStatusException
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

interface IService<T : Entity> {

    val repository: Repo<T>

    fun findById(id: String) =
            repository.findById(id.toUuid())
                    .switchIfEmpty { throw NotFoundStatusException() }

    fun findAll() = repository.findAll()

    fun save(entity: T) = repository.save(entity)

    fun deleteById(id: String) =
            repository.deleteById(id.toUuid())
                    .then()

    fun deleteAll() = repository.deleteAll()

    private fun String.toUuid() =
            try {
                UUID.fromString(this)
            } catch (e: Throwable) {
                throw BadRequestStatusException(e.localizedMessage)
            }
}
