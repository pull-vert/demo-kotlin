package demo.kotlin.web.handlers

import demo.kotlin.entities.User
import demo.kotlin.repositories.USER
import demo.kotlin.services.UserService
import demo.kotlin.web.dtos.UserGetDto
import demo.kotlin.web.dtos.UserSaveDto
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import javax.validation.Validator

@Component
class UserHandler(
        override val service: UserService,
        override val validator: Validator
) : IHandler<User, USER, UserGetDto, UserSaveDto> {

    override fun findById(req: ServerRequest) =
            ServerResponse.ok().body(service.findAuthenticatedUserById(req.pathVariable("id"))
                    .map { authenticatedUser ->
                        UserGetDto(authenticatedUser.username, authenticatedUser.authorities,
                                authenticatedUser.isEnabled, authenticatedUser.id)
                    }, object : ParameterizedTypeReference<UserGetDto>() {})

    override fun saveDtoToEntity(saveDto: UserSaveDto) = User(saveDto.username!!, saveDto.password!!)

    override val findByIdUrl = "/api/users"
}
