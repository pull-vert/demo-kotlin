package demo.kotlin.repositories

import demo.kotlin.entities.Role
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux
import java.util.*

private val logger = KotlinLogging.logger {}

@Repository
class RoleRepository(override val sqlClient: ReactorSqlClient) : Repo<Role>() {

    override fun findAll() = findAllReified()

    override fun findById(id: UUID) = findByIdReified(id)

    override fun count() = countReified()

    override fun deleteAll() = deleteAllReified()

    override fun deleteById(id: UUID) = deleteByIdReified(id)

    override fun createTable() = createTableReified()

    override fun init() =
            arrayOf(Role.ROLE_ADMIN, Role.ROLE_USER)
                    .toFlux()
                    .doOnNext { role -> logger.info { "saving role $role" } }
                    .flatMap { role -> save(role) }
                    .then()
}