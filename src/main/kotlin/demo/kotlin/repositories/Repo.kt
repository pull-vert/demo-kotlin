package demo.kotlin.repositories

import demo.kotlin.entities.Entity
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

abstract class Repo<T : Entity>(protected val sqlClient: ReactorSqlClient) {

    fun save(entity: T) = sqlClient.insert(entity)

    abstract fun findAll(): Flux<T>

    abstract fun findById(id: UUID): Mono<T>

    abstract fun init(): Mono<Void>

    abstract fun count(): Mono<Long>

    abstract fun deleteAll(): Mono<Int>

    abstract fun deleteById(id: UUID): Mono<Int>

    abstract fun createTable(): Mono<Void>

    protected inline fun <reified T : Entity> selectAllReified() =
            sqlClient.selectAll<T>()

    protected inline fun <reified T : Entity> countReified() =
            sqlClient.countAll<T>()

    protected inline fun <reified T : Entity> selectByIdReified(id: UUID) =
            sqlClient.select<T>()
                    .where { it[Entity::id] eq id }
                    .fetchOne()

    protected inline fun <reified T : Entity> deleteAllReified() =
            sqlClient.deleteAllFromTable<T>()

    protected inline fun <reified T : Entity> deleteByIdReified(id: UUID) =
            sqlClient.deleteFromTable<T>()
                    .where { it[Entity::id] eq id }
                    .execute()

    protected inline fun <reified T : Entity> createTableReified() {
        sqlClient.createTable<T>()
    }
}
