# demo-kotlin
This is an example of a Spring boot reactive (Webflux) app in Kotlin, made at first for a live coding and completed since then with new steps.
Every step adds new features.

## [step 0 : initializr](https://github.com/pull-vert/demo-kotlin/tree/master)
Direct commit from Spring Initializr

## [step 1 : project skeleton](https://github.com/pull-vert/demo-kotlin/tree/step1-skeleton)
Empty files for the demo

## [step 2 : mongo reactive](https://github.com/pull-vert/demo-kotlin/tree/step2-mongo-reactive)
MongoDB app is complete (end of live coding) :
* classic Spring annotations (@Service, @Repository...)
* Cow entity (data class entity)
* CowRepository Spring Data Interface
* CowHandler provide Functional functions (= take ServerRequest as parameter and returns Mono<ServerResponse>)
* ApiRoutes provides all Http REST Endpoints of the App
* DatabaseInitializer initializes the embedded MongoDB with test datas
* CowRepositoryTest provides JUnit tests for CowRepository
* ApiTest uses WebTestClient to call real Http REST API

## [step 2 in java](https://github.com/pull-vert/demo-kotlin/tree/step2-mongo-reactive-java)

## [step 2.1 : functional bean registration](https://github.com/pull-vert/demo-kotlin/tree/step2.1-functional-bean-registration)
inspired by : [kofu-reactive-mongodb-sample](https://github.com/spring-projects/spring-fu/tree/master/samples/kofu-reactive-mongodb)
* No more Spring annotations
* Configuration.kt : declare functional bean registration
2018-11-16 : Code is working, but kofu is too limited for now, will give it a try later

## step 3 : HTTP/2
* Create the Keystore and generate a self signed certificate
```bash
 keytool -genkey -noprompt -trustcacerts -keyalg RSA -alias selfsigned -keypass secret -keystore keystore.jks -storepass secret -dname CN=localhost -validity 360 -keysize 2048
```
* Create a certificate signing request (csr)
```bash
keytool -certreq -alias selfsigned -file keystore.csr -keystore keystore.jks
```
*
openssl  x509  -req  -CA ca-certificate.pem.txt -CAkey ca-key.pem.txt -in keystore.csr -out keystore.cer  -days 365  -CAcreateserial
* Export a certificate from a Keystore
```bash
keytool -export -alias selfsigned -file keystore.crt -keystore keystore.jks
Enter keystore password:  secret
Certificate stored in file <keystore.crt>
```
* Create a Truststore with that certificate
```bash
keytool -import -v -trustcacerts -alias selfsigned -file keystore.crt -keystore truststore.jks
Enter keystore password:  secret
Re-enter new password: secret
Owner: CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown
Issuer: CN=Unknown, OU=Unknown, O=Unknown, L=Unknown, ST=Unknown, C=Unknown
...
Trust this certificate? [no]:  yes
Certificate was added to keystore
[Storing truststore.jks]
```
* add HTTP/2 and SSL configuration in application.yaml
