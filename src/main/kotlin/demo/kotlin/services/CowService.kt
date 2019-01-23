package demo.kotlin.services

import demo.kotlin.entities.Cow
import demo.kotlin.repositories.CowRepository
import org.springframework.stereotype.Service

@Service
class CowService(override val repository: CowRepository) : IService<Cow> {

    fun findByName(name: String) = repository.findByName(name)
}