package demo

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import demo.basic.DemoBasicReceiverEndpoint
import demo.jwt.DemoKeyPairReceiverEndpoint
import demo.jwt.DemoPublicKeyEndpoint
import demo.jwt.DemoJwtAuthTokenService
import io.muserver.HttpsConfigBuilder
import io.muserver.MuServerBuilder
import io.muserver.rest.RestHandlerBuilder
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

fun main() {
    startServer(8082)
}

private fun startServer(port: Int) {
    var jwtAuthTokenService = DemoJwtAuthTokenService()
    val server = MuServerBuilder.muServer()
        .withHttpsPort(port)
        .withHttpsConfig(HttpsConfigBuilder.unsignedLocalhost())
        .addHandler(
            RestHandlerBuilder.restHandler(
                DemoBasicReceiverEndpoint(),
                DemoKeyPairReceiverEndpoint { keyPair -> jwtAuthTokenService.keyPair = keyPair },
                DemoPublicKeyEndpoint(jwtAuthTokenService)
            )
                .addCustomReader(
                    JacksonJsonProvider(jacksonObjectMapper()).configure(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        false
                    )
                )
                .addCustomWriter(JacksonJsonProvider(jacksonObjectMapper()))
        )
        .start()

    Runtime.getRuntime().addShutdownHook(Thread { server.stop() })

    logger.info("Server started: ${server.httpsUri()}")
}