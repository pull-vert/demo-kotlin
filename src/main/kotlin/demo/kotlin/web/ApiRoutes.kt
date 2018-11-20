package demo.kotlin.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.*


@Configuration
class ApiRoutes(val cowHandler: CowHandler) {

    @Bean
    fun appRouter(/*httpsRedirectFilter: HandlerFilterFunction<ServerResponse, ServerResponse>*/) = router {
        accept(APPLICATION_JSON).nest {
            "/api/cows".nest {
                GET("/{name}", cowHandler::findByName)
                GET("/", cowHandler::findAll)
            }
        }
    }/*.filter(httpsRedirectFilter)

    @Bean
    fun httpsRedirectFilter() = object: HandlerFilterFunction<ServerResponse, ServerResponse> {
        override fun filter(request: ServerRequest, next: HandlerFunction<ServerResponse>): Mono<ServerResponse> {
                        val originalUri = request.uri()

//            here set your condition to http->https redirect
            val forwardedValues = request.headers().header("x-forwarded-proto")
            if (forwardedValues.contains("http")) {
                try {
                    val mutatedUri = URI("https",
                            originalUri.userInfo,
                            originalUri.host,
                            originalUri.port,
                            originalUri.path,
                            originalUri.query,
                            originalUri.fragment)
                    return ServerResponse.permanentRedirect(mutatedUri).build()
                } catch (e: URISyntaxException) {
                    throw IllegalStateException(e.message, e)
                }
            }
        return next.handle(request)
        }
    }*/
}
