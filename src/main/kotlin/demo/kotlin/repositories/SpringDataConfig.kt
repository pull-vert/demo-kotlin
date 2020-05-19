package demo.kotlin.repositories

import com.github.michaelbull.logging.InlineLogger
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import demo.kotlin.database.EmbeddedMongo
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory
import org.springframework.data.mongodb.ReactiveMongoTransactionManager
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

const val DB_NAME = "MONGO_DB_WITH_SESSION"

@Configuration
@EnableMongoAuditing
@EnableReactiveMongoRepositories
@EnableTransactionManagement
class SpringDataConfig : AbstractReactiveMongoConfiguration() {

    private val logger = InlineLogger()

    private val replSet = EmbeddedMongo.replSet().configure()

    @PostConstruct
    fun onStart() {
        try {
            replSet.start()
        } catch (e: RuntimeException) {
            logger.error(e) { "Cannot start MongoDB" }
        }
    }

    @PreDestroy
    fun onExit() {
        try {
            replSet.stop()
        } catch (e: RuntimeException) {
            logger.error(e) { "Cannot stop MongoDB" }
        }
    }

    @Bean
    fun transactionManager(factory: ReactiveMongoDatabaseFactory): ReactiveTransactionManager {
        return ReactiveMongoTransactionManager(factory)
    }

    @Bean
    override fun reactiveMongoClient(): MongoClient {
        return MongoClients.create(replSet.connectionString())
    }

    override fun getDatabaseName() = DB_NAME
}
