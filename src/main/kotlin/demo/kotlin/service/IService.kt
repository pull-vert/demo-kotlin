package demo.kotlin.service

import demo.kotlin.web.BadRequestStatusException
import demo.kotlin.web.NotFoundStatusException
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.switchIfEmpty
import java.util.*

interface IService<T> {

    val repository: ReactiveMongoRepository<T, UUID>

    fun findById(id: String) =
            repository.findById(id.toUuid())
                    .switchIfEmpty { throw NotFoundStatusException() }

    fun findAll() = repository.findAll()

    fun save(entity: T) = repository.save(entity)

    fun deleteById(id: String) = repository.deleteById(id.toUuid())

    private fun String?.toUuid() =
            try {
                UUID.fromString(this)
            } catch(e: Throwable) {
                throw BadRequestStatusException(e.localizedMessage)
            }
}