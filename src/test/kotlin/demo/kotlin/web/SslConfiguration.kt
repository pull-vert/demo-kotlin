//package demo.kotlin.web
//
//import io.netty.handler.codec.http2.Http2SecurityUtil
//import io.netty.handler.ssl.*
//import io.netty.handler.ssl.util.InsecureTrustManagerFactory
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.context.annotation.Profile
//import org.springframework.http.client.reactive.ReactorClientHttpConnector
//import org.springframework.test.web.reactive.server.WebTestClient
//import org.springframework.util.ResourceUtils
//import reactor.netty.http.client.HttpClient
//import sun.security.util.UntrustedCertificates
//import java.io.FileInputStream
//import java.security.KeyStore
//import java.security.KeyStoreException
//import java.security.PrivateKey
//import java.security.cert.X509Certificate
//
//
//@Configuration
//@Profile("test")
//class SslConfiguration {
//
//    @Bean
//    fun configureWebclient(
//            @Value("\${server.ssl.trust-store}") trustStorePath: String,
//            @Value("\${server.ssl.trust-store-password}") trustStorePass: String,
//            @Value("\${server.ssl.key-store}") keyStorePath: String,
//            @Value("\${server.ssl.key-store-password}") keyStorePass: String,
//            @Value("\${server.ssl.key-alias}") keyAlias: String
//    ): WebTestClient.Builder {
//        val trustStore = KeyStore.getInstance(KeyStore.getDefaultType())
//        trustStore.load(FileInputStream(ResourceUtils.getFile(trustStorePath)), trustStorePass.toCharArray())
//
//        val certificateCollection = trustStore.aliases().asSequence().filter {
//            try {
//                trustStore.isCertificateEntry(it)
//            } catch (e1: KeyStoreException) {
//                throw RuntimeException("Error reading truststore", e1)
//            }
//        }.map {
//            try {
//                trustStore.getCertificate(it) as X509Certificate
//            } catch (e2: KeyStoreException) {
//                throw RuntimeException("Error reading truststore", e2)
//            }
//        }.toList()
//
//        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
//        keyStore.load(FileInputStream(ResourceUtils.getFile(keyStorePath)), keyStorePass.toCharArray())
//        val provider = if (OpenSsl.isAlpnSupported())
//            io.netty.handler.ssl.SslProvider.OPENSSL
//        else
//            io.netty.handler.ssl.SslProvider.JDK
//        val sslContext = SslContextBuilder.forClient()
//                .sslProvider(provider)
//                .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
//                .applicationProtocolConfig(ApplicationProtocolConfig(
//                        ApplicationProtocolConfig.Protocol.ALPN,
//                        ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
//                        ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
//                        ApplicationProtocolNames.HTTP_2,
//                        ApplicationProtocolNames.HTTP_1_1))
////                .keyManager(keyStore.getKey(keyAlias, keyStorePass.toCharArray()) as PrivateKey)
//                .trustManager(/*certificateCollection.toTypedArray()*/InsecureTrustManagerFactory.INSTANCE)
//                .build()
//        val httpConnector = HttpClient.create().secure { t -> t.sslContext(sslContext) }
//        return WebTestClient.bindToServer(ReactorClientHttpConnector(httpConnector))
//    }
//}