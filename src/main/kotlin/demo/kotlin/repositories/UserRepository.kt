package demo.kotlin.repositories

import demo.kotlin.entities.User
import mu.KotlinLogging
import org.springframework.stereotype.Repository
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.kotlin.core.publisher.toFlux
import java.util.*

private val logger = KotlinLogging.logger {}

@Repository
class UserRepository(override val sqlClient: ReactorSqlClient) : Repo<User>() {

    override fun findAll() = findAllReified()

    override fun findById(id: UUID) = findByIdReified(id)

    override fun count() = countReified()

    override fun deleteAll() = deleteAllReified()

    override fun deleteById(id: UUID) = deleteByIdReified(id)

    override fun createTable() = createTableReified()

    fun findByUsername(username: String) =
            sqlClient.select<User>()
                    .where { it[User::username] eq username }
                    .fetchFirst()

    override fun init() =
            arrayOf(fred, boss, userToDelete)
                    .toFlux()
                    .doOnNext { user -> logger.info { "saving user $user" } }
                    .flatMap { user -> save(user) }
                    .then()
}

internal val USER_FRED_UUID = UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f")
internal val USER_BOSS_UUID = UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e")
internal val USER_TO_DELETE_UUID = UUID.fromString("856748fd-5d2e-4559-a130-73c34fbc3d3a")
internal val fred = User("Fred", "password", id = USER_FRED_UUID, enabled = true)
internal val boss = User("Boss", "secured_password", id = USER_BOSS_UUID, enabled = true)
internal val userToDelete = User("to_delete", "to_delete", id = USER_TO_DELETE_UUID)
