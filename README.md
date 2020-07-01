# demo-kotlin
This is an example of a Spring boot reactive (Webflux) app in Kotlin, made at first for a live coding and completed since then with new steps.
Every step adds new features.

[Master](https://github.com/pull-vert/demo-kotlin) contains the most recent finished feature.

## Kotlin with Spring annotations (@Service, @Repository...)

### [step 1 : project skeleton](https://github.com/pull-vert/demo-kotlin/tree/step1-skeleton)
Empty files for the demo

### [step 2 : mongo reactive](https://github.com/pull-vert/demo-kotlin/tree/step2-mongo-reactive)
MongoDB app is complete (end of live coding) :
* classic Spring annotations (@Service, @Repository...)
* Cow entity (data class entity)
* CowRepository Spring Data Interface
* CowHandler provide Functional functions (= take ServerRequest as parameter and returns Mono<ServerResponse>)
* ApiRoutes provides all Http REST Endpoints of the App
* DatabaseInitializer initializes the embedded MongoDB with test datas
* CowRepositoryTest provides JUnit tests for CowRepository
* ApiTest uses WebTestClient to call real Http REST API

### [step 3 : HTTP/2](https://github.com/pull-vert/demo-kotlin/tree/step3-http-2)
* Generate a self signed Keystore
```bash
keytool -genkey -keyalg RSA -alias selfsigned -keystore keystore.jks -storepass secret -dname CN=localhost -validity 360 -keysize 2048
```
* stored it in src/main/resources
* add HTTP/2 and SSL configuration in application.yml
* change ApiTest (InsecureTrustManagerFactory.INSTANCE) to communicate with self signed server keystore
* netty-tcnative-boringssl-static dependency is only required for Java 8, since java 9 ALPN is natively supported
* For production : make a POST and a GET endpoint for let's encrypt HTTP challenge, or use a real well signed Certificate

### [step 4 : Spring Restdocs](https://github.com/pull-vert/demo-kotlin/tree/step4-spring-restdocs)
* Modify build.gradle to add asciidoc and Spring Restdocs
* Modify ApiTest to generate doc
* add index.adoc to expose all API documentation

### [step 5 : Spring security with JWT](https://github.com/pull-vert/demo-kotlin/tree/step5-spring-security)
* New package demo.kotlin.security
* Security and Roles for endpoints is configured in SecurityConfig class
* New User entity, with UserRepository, UserService and UserHandler
* New AuthenticationHandler for POST /auth
* Followed these tutorials : [jwt-on-spring-webflux](https://medium.com/@ard333/authentication-and-authorization-using-jwt-on-spring-webflux-29b81f813e78)
* https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#reactive-applications
* https://spring.io/guides/topicals/spring-security-architecture

### [step 6 : DTOs + bean validation](https://github.com/pull-vert/demo-kotlin/tree/step6-dtos-bean-validation)
* now webservice endpoints consume and return DTOs
* mapping between POJOs is made by Extension Functions
* Bean validation with annotations
* Note : all fields for Request DTO must be nullable to prevent Jackson deserialize error if param is null. Bean validation will take care of returning a nice error to API client
* Add constraints documentation in Spring Restdocs based on Bean validation annotations

### [step 7 : R2DBC + Kotysa](https://github.com/pull-vert/demo-kotlin/tree/step7-r2dbc-with-kotysa)
* inspired by : [spring with r2dbc](https://spring.io/guides/gs/accessing-data-r2dbc/)
* uses [Kotysa SqlClient](https://github.com/ufoss-org/kotysa)

## Kotlin with Functional bean registration
### [step 2.1 : functional bean registration](https://github.com/pull-vert/demo-kotlin/tree/step2.1-functional-bean-registration)
inspired by : [kofu-reactive-mongodb-sample](https://github.com/spring-projects-experimental/spring-fu/tree/master/samples/kofu-reactive-mongodb)
* No more Spring annotations
* Configuration.kt : declare functional bean registration

## Java with Spring annotations (@Service, @Repository...)
### [step 2 in java](https://github.com/pull-vert/demo-kotlin/tree/step2-mongo-reactive-java)
Allows to compare Java vs Kotlin code
