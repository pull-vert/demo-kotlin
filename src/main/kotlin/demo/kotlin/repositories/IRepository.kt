package demo.kotlin.repositories

import demo.kotlin.entities.IEntity
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import java.util.*

interface IRepository<T : IEntity> : ReactiveMongoRepository<T, UUID>