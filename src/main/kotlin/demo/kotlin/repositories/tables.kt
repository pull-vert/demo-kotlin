package demo.kotlin.repositories

import demo.kotlin.entities.Cow
import demo.kotlin.entities.Role
import demo.kotlin.entities.User
import demo.kotlin.entities.UserRole
import org.ufoss.kotysa.tables

internal val tables = tables()
        .h2 {
            table<User> {
                column { it[User::id].uuid() }.primaryKey()
                column { it[User::username].varchar() }
                column { it[User::password].varchar() }
                column { it[User::enabled].boolean() }
            }
            table<Role> {
                column { it[Role::id].uuid() }.primaryKey()
                column { it[Role::name].varchar() }
            }
            table<UserRole> {
                primaryKey(
                        column { it[UserRole::userId].uuid() }.foreignKey<User>(),
                        column { it[UserRole::roleId].uuid() }.foreignKey<Role>()
                )
            }
            table<Cow> {
                column { it[Cow::id].uuid() }.primaryKey()
                column { it[Cow::name].varchar() }
                column { it[Cow::lastCalvingDate].date() }
            }
        }
