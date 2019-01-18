package demo.kotlin.service

import demo.kotlin.model.Cow
import demo.kotlin.repository.CowRepository
import org.springframework.stereotype.Service

@Service
class CowService(override val repository: CowRepository) : IService<Cow> {

    fun findByName(name: String) = repository.findByName(name)
}