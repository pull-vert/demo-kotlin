package demo.kotlin.repositories

import demo.kotlin.entities.User
import org.springframework.stereotype.Repository
import org.ufoss.kotysa.r2dbc.ReactorSqlClient

@Repository
class UserRepository(override val sqlClient: ReactorSqlClient) : Repo<User>(sqlClient) {

}
