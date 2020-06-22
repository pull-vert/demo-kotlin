package demo.kotlin.repositories

import demo.kotlin.entities.Role
import demo.kotlin.entities.User
import org.ufoss.kotysa.tables

internal val tables = tables()
        .h2 {
            table<Role> {
                column { it[Role::id].uuid() }.primaryKey()
                column { it[Role::name].varchar() }
            }
        }
