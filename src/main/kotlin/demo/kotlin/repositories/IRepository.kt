package demo.kotlin.repositories

import demo.kotlin.entities.Entity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.util.*

interface IRepository<T : Entity> : ReactiveMongoRepository<T, UUID>