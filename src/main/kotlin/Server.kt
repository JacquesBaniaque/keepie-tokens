import api.HealthEndpoint
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider
import io.muserver.HttpsConfigBuilder
import io.muserver.MuServer
import io.muserver.MuServerBuilder.muServer
import io.muserver.rest.RestHandlerBuilder
import mu.KotlinLogging


class Server {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private lateinit var server: MuServer

    fun start() {
        server = muServer()
            .withHttpsPort(8080)
            .withHttpsConfig(HttpsConfigBuilder.unsignedLocalhost())
            .addHandler(
                RestHandlerBuilder.restHandler(
                    HealthEndpoint()
                )
                    .addCustomReader(JacksonJaxbJsonProvider().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
                    .addCustomWriter(JacksonJaxbJsonProvider())
            )
            .start()

        logger.info("Server started: ${server.httpsUri()}")
    }

    fun stop() {
        server.stop()
    }
}