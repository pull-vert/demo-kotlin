package demo.kotlin.repositories

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.DatabaseClient
import org.ufoss.kotysa.r2dbc.sqlClient


@Configuration
class SpringDataConfig {

    @Bean
    fun sqlClient(dbClient: DatabaseClient) = dbClient.sqlClient(tables)
}
