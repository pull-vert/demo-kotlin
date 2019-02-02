package demo.kotlin.web.handlers

import demo.kotlin.entities.Entity
import demo.kotlin.services.IService
import demo.kotlin.web.dtos.IDto
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.bodyToMono
import java.net.URI

interface IHandler<T : Entity, GET_DTO : IDto, SAVE_DTO : IDto> : Validate {

    val service: IService<T>

    /**
     * Only override this if save is used
     */
    val findByIdUrl
        get() = "override_findByIdUrl_val_with_actual_Url"

    fun entityToGetDto(entity: T): GET_DTO
    fun saveDtoToEntity(saveDto: SAVE_DTO): T

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
inline fun <T : Entity, GET_DTO : IDto, reified SAVE_DTO : IDto> IHandler<T, GET_DTO, SAVE_DTO>.save(req: ServerRequest) =
        req.bodyToMono<SAVE_DTO>()
                .doOnNext(::callValidator)
                .map(::saveDtoToEntity)
                .flatMap { entity -> service.save(entity) }
                .flatMap { entity -> created(URI.create("$findByIdUrl/${entity.id}")).build() }