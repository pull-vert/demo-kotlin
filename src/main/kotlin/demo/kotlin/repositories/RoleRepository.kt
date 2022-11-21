package demo.kotlin.repositories

import demo.kotlin.entities.Role
import org.springframework.stereotype.Repository
import org.ufoss.kolog.Logger
import org.ufoss.kotysa.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux

private val logger = Logger.of<RoleRepository>()

@Repository
class RoleRepository(override val sqlClient: ReactorSqlClient) : Repo<Role, ROLE> {

    override val table = ROLE

    override fun init() =
            arrayOf(Role.ROLE_ADMIN, Role.ROLE_USER)
                    .toFlux()
                    .doOnNext { role -> logger.info { "saving role $role" } }
                    .flatMap { role -> save(role) }
                    .then()
}