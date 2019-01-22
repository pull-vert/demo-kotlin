package demo.kotlin.security

import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class CORSConfig : WebFluxConfigurer {

    /**
     * For dev only : avoid CORS errors for local tests
     */
    override fun addCorsMappings(registry: CorsRegistry) {
        registry
                .addMapping("/**")
                .allowedMethods("*")
//                .allowedOrigins("*") // todo check if these 2 commented lines are needed
//                .allowedHeaders("*")
    }
}
