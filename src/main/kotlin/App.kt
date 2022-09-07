import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jwt.TokenService
import keepie.KeepieService
import keepie.config.ServicesConfig
import mu.KotlinLogging
import java.time.Duration

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    val servicesConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/services.conf")
        .build()
        .loadConfigOrThrow<ServicesConfig>()
    val keepieService = KeepieService(services = servicesConfig.services)
    val tokenService = TokenService(
        Keys.keyPairFor(SignatureAlgorithm.RS256),
        Duration.ofSeconds(10)
    )
    val server = Server(
        tokenService = tokenService,
        keepieService = keepieService
    )
    server.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
        logger.info("Server stopped")
    })
}