package demo.kotlin.repositories

import demo.kotlin.entities.User
import org.springframework.stereotype.Repository
import org.ufoss.kotysa.r2dbc.ReactorSqlClient
import reactor.core.publisher.Mono
import java.util.*

@Repository
class UserRepository(override val sqlClient: ReactorSqlClient) : Repo<User, USER> {

    override val table = USER

    fun findByUsername(username: String) =
            (sqlClient selectFrom USER
                    where USER.username eq username
                    ).fetchOne()

    override fun init(): Mono<Void> {
        throw NotImplementedError()
    }
}

internal val USER_FRED_UUID = UUID.fromString("79e9eb45-2835-49c8-ad3b-c951b591bc7f")
internal val USER_BOSS_UUID = UUID.fromString("67d4306e-d99d-4e54-8b1d-5b1e92691a4e")
internal val USER_TO_DELETE_UUID = UUID.fromString("856748fd-5d2e-4559-a130-73c34fbc3d3a")
