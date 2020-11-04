package demo.kotlin.repositories

import demo.kotlin.entities.Role
import demo.kotlin.entities.UserRole
import org.springframework.stereotype.Repository
import org.ufoss.kolog.Logger
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux
import java.util.*

private val logger = Logger.of<UserRoleRepository>()

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
            arrayOf(
                    UserRole(USER_FRED_UUID, Role.ROLE_USER.id),
                    UserRole(USER_BOSS_UUID, Role.ROLE_ADMIN.id)
            )
                    .toFlux()
                    .doOnNext { userRole -> logger.info { "saving userRole $userRole" } }
                    .flatMap { userRole -> save(userRole) }
                    .then()
}
