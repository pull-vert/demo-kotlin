package demo.kotlin.web.handlers

import demo.kotlin.entities.Entity
import demo.kotlin.repositories.ENTITY
import demo.kotlin.services.IService
import demo.kotlin.web.dtos.IDto
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.bodyToMono
import java.net.URI

interface IHandler<T : Entity, U : ENTITY<T>, GET_DTO : IDto, SAVE_DTO : IDto> : Validate {

    val service: IService<T, U>

    /**
     * Only override this if save is used
     */
    val findByIdUrl
        get(): String = TODO("override_findByIdUrl_val_with_actual_Url")

    fun entityToGetDto(entity: T): GET_DTO {
        TODO("override_entityToGetDto_func_if_needed")
    }
    fun saveDtoToEntity(saveDto: SAVE_DTO): T {
        TODO("override_saveDtoToEntity_func_if_needed")
    }

    fun findById(req: ServerRequest) =
            ok().body(service.findById(req.pathVariable("id"))
                    .map(::entityToGetDto)
                    , object : ParameterizedTypeReference<GET_DTO>() {})

    fun findAll(req: ServerRequest) =
            ok().body(service.findAll()
                    .map(::entityToGetDto)
                    , object : ParameterizedTypeReference<GET_DTO>() {})

    fun deleteById(req: ServerRequest) =
            noContent().build(service.deleteById(req.pathVariable("id")))
}

// Must use inline function and reified because of bodyToMono<> not working with normal interface fun
inline fun <T : Entity, U : ENTITY<T>, GET_DTO : IDto, reified SAVE_DTO : IDto> IHandler<T, U, GET_DTO, SAVE_DTO>.save(req: ServerRequest) =
        req.bodyToMono<SAVE_DTO>()
                .doOnNext(::callValidator)
                .map(::saveDtoToEntity)
                .flatMap { entity -> service.save(entity).thenReturn(entity) }
                .flatMap { entity -> created(URI.create("$findByIdUrl/${entity.id}")).build() }
