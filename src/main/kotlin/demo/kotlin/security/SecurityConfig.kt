package demo.kotlin.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
        private val authenticationManager: AuthenticationManager,
        private val securityContextRepository: SecurityContextRepository
) {

    /**
     * The default password encoder, used for encoding password in User Document
     * And used to decode password in Spring security Authentication
     */
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun securitygWebFilterChain(http: ServerHttpSecurity) =
            http.csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .csrf().disable()
                    .logout().disable()
                    .authenticationManager(authenticationManager)
                    .securityContextRepository(securityContextRepository)
                    .authorizeExchange()
                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .pathMatchers(HttpMethod.POST,"/auth", "/api/users").permitAll()
                    .pathMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasRole("ADMIN")
                    .pathMatchers("/api/**").hasRole("USER")
                    .anyExchange().authenticated()
                    .and().build()
}
