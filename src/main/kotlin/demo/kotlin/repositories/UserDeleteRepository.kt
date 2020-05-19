package demo.kotlin.repositories

import demo.kotlin.entities.User
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Repository
class UserDeleteRepository(
        private val reactiveOps: ReactiveMongoOperations
) {

    @Transactional
    fun deleteById(id: String): Mono<Void> =
            reactiveOps.remove(Query(Criteria.where("id").`is`(id)), User::class.java).then()
}
