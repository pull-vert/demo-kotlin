package demo.kotlin.services

import demo.kotlin.entities.Entity
import demo.kotlin.repositories.IRepository
import demo.kotlin.web.BadRequestStatusException
import demo.kotlin.web.NotFoundStatusException
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

interface IService<T : Entity> {

    val repository: IRepository<T>

    fun findById(id: String) =
            repository.findById(id.checkValidUuid())
                    .switchIfEmpty { throw NotFoundStatusException() }

    fun findAll() = repository.findAll()

    fun save(entity: T) = repository.save(entity)

    fun deleteById(id: String) = repository.deleteById(id.checkValidUuid())

    fun deleteAll() = repository.deleteAll()

    private fun String.checkValidUuid() =
            try {
                UUID.fromString(this)
                this
            } catch (e: Throwable) {
                throw BadRequestStatusException(e.localizedMessage)
            }
}
