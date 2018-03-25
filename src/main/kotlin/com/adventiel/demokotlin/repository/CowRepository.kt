package com.adventiel.demokotlin.repository

import com.adventiel.demokotlin.model.Cow
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

//@Repository
//interface CowRepository : ReactiveMongoRepository<Cow, UUID> {
//    fun findByName(name: String): Mono<Cow>
//}