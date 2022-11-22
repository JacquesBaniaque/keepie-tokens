import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jwt.TokenService
import keepie.KeepieService
import keepie.config.SecretsAccessConfig
import keepie.config.SecretsConfig
import keepie.config.ServicesConfig
import mu.KotlinLogging
import org.apache.commons.text.RandomStringGenerator
import java.time.Duration

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    val servicesConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/services.conf")
        .build()
        .loadConfigOrThrow<ServicesConfig>()
    val secretsConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/secrets.conf")
        .build()
        .loadConfigOrThrow<SecretsConfig>()
        .normalize()
    val secretsAccessConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/secrets-access.conf")
        .build()
        .loadConfigOrThrow<SecretsAccessConfig>()
    val generators = mapOf("random-string" to RandomStringGenerator.Builder()
        .withinRange(
            charArrayOf('a', 'z'),
            charArrayOf('A', 'Z'),
            charArrayOf('0', '9')
        )
        .build())

    servicesConfig.print()
    secretsConfig.print()
    secretsAccessConfig.print()

    val keepieService = KeepieService(
        services = servicesConfig.services,
        secrets = secretsConfig.secrets,
        servicesToSecrets = secretsAccessConfig.servicesToSecrets,
        generators = generators)
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