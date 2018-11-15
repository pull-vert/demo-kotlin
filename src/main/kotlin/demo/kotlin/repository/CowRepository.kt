package demo.kotlin.repository

import demo.kotlin.model.Cow
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.findAll
import org.springframework.data.mongodb.core.findOne
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo

internal class CowRepository (
        private val mongo: ReactiveMongoOperations
) {
    fun findAll() = mongo.findAll<Cow>()

    fun save(cow: Cow) = mongo.save(cow)

    fun findByName(name: String) = mongo.findOne<Cow>(Query(Criteria("name").isEqualTo(name)))
}
