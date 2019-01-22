package demo.kotlin.web.handlers

import demo.kotlin.model.entities.User
import demo.kotlin.services.UserService
import org.springframework.stereotype.Component

@Component
class UserHandler(override val service: UserService): IHandler<User> {

    override val findByIdUrl: String = "/api/users"

    // todo when Mapstruct is here save must not provide a ID
}
