package demo.kotlin.web

import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.router

internal fun routes(cowHandler: CowHandler) = router {
    accept(APPLICATION_JSON).nest {
        "/api/cows".nest {
            GET("/{name}", cowHandler::findByName)
            GET("/", cowHandler::findAll)
        }
    }
}
