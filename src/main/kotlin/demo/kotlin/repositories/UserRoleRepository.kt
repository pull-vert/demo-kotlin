package demo.kotlin.repositories

import demo.kotlin.entities.Role
import demo.kotlin.entities.UserRole
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux
import java.util.*

private val logger = KotlinLogging.logger {}

@Repository
class UserRoleRepository(private val sqlClient: ReactorSqlClient) {

    fun deleteAll() = sqlClient.deleteAllFromTable<UserRole>()

    fun save(entity: UserRole) = sqlClient.insert(entity)

    fun createTable() = sqlClient.createTable<UserRole>()

    fun findRoleIdsByUserId(userId: UUID) =
            sqlClient.select { it[UserRole::roleId] }
                    .where { it[UserRole::userId] eq userId }
                    .fetchAll()

    fun init() =
            createTable()
                    .then(deleteAll())
                    .then(arrayOf(fredUser, bossAdmin)
                            .toFlux()
                            .doOnNext { userRole -> logger.info { "saving userRole $userRole" } }
                            .flatMap { userRole -> save(userRole) }
                            .then()
                    )
}

internal val fredUser = UserRole(USER_FRED_UUID, Role.ROLE_USER.id)
internal val bossAdmin = UserRole(USER_BOSS_UUID, Role.ROLE_ADMIN.id)
