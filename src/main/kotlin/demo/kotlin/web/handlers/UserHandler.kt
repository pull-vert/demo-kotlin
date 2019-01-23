package demo.kotlin.web.handlers

import demo.kotlin.entities.User
import demo.kotlin.services.UserService
import demo.kotlin.web.dtos.UserGetDto
import demo.kotlin.web.dtos.UserSaveDto
import org.springframework.stereotype.Component

@Component
class UserHandler(override val service: UserService): IHandler<User, UserGetDto, UserSaveDto> {

    override fun entityToGetDto(entity: User) = UserGetDto(entity.username, entity.authorities, entity.isEnabled, entity.id.toString())

    override fun saveDtoToEntity(saveDto: UserSaveDto) = User(saveDto.username, saveDto.password)

    override val findByIdUrl: String = "/api/users"
}
