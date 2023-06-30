import api.HealthEndpoint
import api.KeepieEndpoint
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.muserver.HttpsConfigBuilder
import io.muserver.MuServer
import io.muserver.MuServerBuilder.muServer
import io.muserver.rest.RestHandlerBuilder
import keepie.KeepieService
import mu.KotlinLogging


class Server(
    private val keepieService: KeepieService
) {

    companion object {
        private val logger = KotlinLogging.logger {}
    }

    private lateinit var server: MuServer

    fun start(port: Int = 8080) {
        server = muServer()
            .withHttpsPort(port)
            .withHttpsConfig(HttpsConfigBuilder.unsignedLocalhost())
            .addHandler(
                RestHandlerBuilder.restHandler(
                    KeepieEndpoint(keepieService = keepieService),
                    HealthEndpoint()
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
        logger.info("Server started: ${server.httpsUri()}")
    }

    fun stop() {
        server.stop()
    }
}