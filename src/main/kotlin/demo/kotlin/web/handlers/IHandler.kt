package demo.kotlin.web.handlers

import demo.kotlin.model.entities.IEntity
import demo.kotlin.services.IService
import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.bodyToMono
import java.net.URI

interface IHandler<T : IEntity> {

    val service: IService<T>

    /**
     * Only override this if save is used
     */
    val findByIdUrl
            get() = "override_findByIdUrl_val_with_actual_Url"

    fun findById(req: ServerRequest)=
            ok().body(service.findById(req.pathVariable("id")), object : ParameterizedTypeReference<T>() {})

    fun findAll(req: ServerRequest)=
            ok().body(service.findAll(), object : ParameterizedTypeReference<T>() {})

    fun deleteById(req: ServerRequest) =
            noContent().build(service.deleteById(req.pathVariable("id")))
}

// Must use inline function because of
inline fun <reified T : IEntity> IHandler<T>.save(req: ServerRequest) =
        req.bodyToMono<T>()
                .flatMap { service.save(it) }
                .map { it.id }
                .flatMap { created(URI.create("$findByIdUrl/$it")).build() }
