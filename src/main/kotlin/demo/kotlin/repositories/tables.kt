package demo.kotlin.repositories

import demo.kotlin.entities.*
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.UuidColumnNotNull
import org.ufoss.kotysa.h2.H2Table
import org.ufoss.kotysa.tables

internal val tables = tables().h2(USER, ROLE, USER_ROLE, COW)

interface ENTITY<T : Entity> : Table<T> {
    val id: UuidColumnNotNull<T>
}

object ROLE : H2Table<Role>(), ENTITY<Role> {
    override val id = uuid(Role::id)
            .primaryKey()
    val authority = varchar(Role::getAuthority)
}

object USER : H2Table<User>("Users"), ENTITY<User> {
    override val id = uuid(User::id)
            .primaryKey()
    val username = varchar(User::username)
    val password = varchar(User::password)
    val enabled = boolean(User::enabled)
}

object USER_ROLE : H2Table<UserRole>() {
    val userId = uuid(UserRole::userId)
            .foreignKey(USER.id)
    val roleId = uuid(UserRole::roleId)
            .foreignKey(ROLE.id)
    val pk = primaryKey(userId, roleId)
}

object COW : H2Table<Cow>(), ENTITY<Cow> {
    override val id = uuid(Cow::id)
            .primaryKey()
    val name = varchar(Cow::name)
    val lastCalvingDate = date(Cow::lastCalvingDate)
}
