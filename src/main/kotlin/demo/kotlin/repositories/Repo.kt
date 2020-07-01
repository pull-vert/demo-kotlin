package demo.kotlin.repositories

import demo.kotlin.entities.Entity
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

abstract class Repo<T : Entity> {

    internal abstract val sqlClient: ReactorSqlClient

    fun save(entity: T) = sqlClient.insert(entity)

    abstract fun findAll(): Flux<T>

    abstract fun findById(id: UUID): Mono<T>

    abstract fun init(): Mono<Void>

    abstract fun count(): Mono<Long>

    abstract fun deleteAll(): Mono<Int>

    abstract fun deleteById(id: UUID): Mono<Int>

    abstract fun createTable(): Mono<Void>
}

internal inline fun <reified T : Entity> Repo<T>.findAllReified() = sqlClient.selectAll<T>()

internal inline fun <reified T : Entity> Repo<T>.countReified() = sqlClient.countAll<T>()

internal inline fun <reified T : Entity> Repo<T>.findByIdReified(id: UUID) =
        sqlClient.select<T>()
                .where { it[Entity::id] eq id }
                .fetchOne()

internal inline fun <reified T : Entity> Repo<T>.deleteAllReified() = sqlClient.deleteAllFromTable<T>()

internal inline fun <reified T : Entity> Repo<T>.deleteByIdReified(id: UUID) =
        sqlClient.deleteFromTable<T>()
                .where { it[Entity::id] eq id }
                .execute()

internal inline fun <reified T : Entity> Repo<T>.createTableReified() = sqlClient.createTable<T>()
