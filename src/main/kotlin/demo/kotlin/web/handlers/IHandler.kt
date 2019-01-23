package demo.kotlin.web.handlers

import demo.kotlin.model.dtos.IDto
import demo.kotlin.model.entities.IEntity
import demo.kotlin.services.IService
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.bodyToMono
import java.net.URI

interface IHandler<T : IEntity, U : IDto> {

    val service: IService<T>

    /**
     * Only override this if save is used
     */
    val findByIdUrl
            get() = "override_findByIdUrl_val_with_actual_Url"

    val entityToDto: (T) -> U

    fun findById(req: ServerRequest) =
            ok().body(service.findById(req.pathVariable("id")).map(entityToDto), object : ParameterizedTypeReference<U>() {})

    fun findAll(req: ServerRequest) =
            ok().body(service.findAll().map(entityToDto), object : ParameterizedTypeReference<U>() {})

    fun deleteById(req: ServerRequest) =
            noContent().build(service.deleteById(req.pathVariable("id")))
}

// Must use inline function because of
inline fun <reified T : IEntity, U : IDto> IHandler<T, U>.save(req: ServerRequest) =
        req.bodyToMono<T>()
                .flatMap { service.save(it) }
                .map { it.id }
                .flatMap { created(URI.create("$findByIdUrl/$it")).build() }
