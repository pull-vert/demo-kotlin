package demo.kotlin

import demo.kotlin.repositories.CowRepository
import demo.kotlin.repositories.RoleRepository
import demo.kotlin.repositories.UserRepository
import demo.kotlin.repositories.UserRoleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class DatabaseInitializer(
        private val cowRepository: CowRepository,
        private val userRepository: UserRepository,
        private val roleRepository: RoleRepository,
        private val userRoleRepository: UserRoleRepository
) : CommandLineRunner {

    override fun run(vararg args: String) {
        println("DatabaseInitializer start")

        cowRepository.init()
                .then(userRepository.init())
                .then(roleRepository.init())
                .then(userRoleRepository.init())
                .block()

        println("cows size = " + cowRepository.findAll()
                .collectList()
                .block()!!.size)
    }
}
