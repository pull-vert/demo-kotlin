package demo.kotlin.repositories

import demo.kotlin.entities.Entity
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.core.publisher.Mono
import java.util.*

interface Repo<T : Entity, U : ENTITY<T>> {

    val sqlClient: ReactorSqlClient
    val table: U

    fun save(entity: T) = sqlClient insert entity

    fun findAll() = sqlClient selectAllFrom table

    fun findById(id: UUID) =
            (sqlClient selectFrom table
                    where table.id eq id
                    ).fetchOne()

    fun countAll() = sqlClient selectCountAllFrom table

    fun deleteAll() = sqlClient deleteAllFrom table

    fun deleteById(id: UUID) =
            (sqlClient deleteFrom table
                    where table.id eq id
                    ).execute()

    fun createTable() = sqlClient createTableIfNotExists table

    fun init(): Mono<Void>
}
