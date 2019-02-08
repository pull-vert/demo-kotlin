package demo.kotlin.repositories

import demo.kotlin.entities.Entity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface IRepository<T : Entity> : ReactiveMongoRepository<T, String>
