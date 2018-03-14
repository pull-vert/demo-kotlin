# demo-kotlin

## step 0
Direct commit from Spring Initializr

## step 1
Empty components for the demo

## step 2
App is complete :
* Cow entity (data class entity)
* CowRepository Spring Data Interface
* CowHandler provide Functional functions (= take ServerRequest as parameter and returns Mono<ServerResponse>)
* ApiRoutes provides all Http REST Endpoints of the App
* DatabaseInitializer initializes the embedded MongoDB with test datas
* CowRepositoryTest provides JUnit tests for CowRepository
* ApiTest uses WebClient to call real Http REST API
