import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jwt.TokenService
import keepie.KeepieService
import keepie.config.SecretsAccessConfig
import keepie.config.SecretsConfig
import keepie.config.ServicesConfig
import keepie.generators.KeepieGenerator
import keepie.generators.RandomStringGenerator
import mu.KotlinLogging
import java.time.Duration

private val logger = KotlinLogging.logger {}

fun main() {
    val servicesConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/services.conf")
        .build()
        .loadConfigOrThrow<ServicesConfig>()
    val secretsConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/secrets.conf")
        .build()
        .loadConfigOrThrow<SecretsConfig>()
    val secretsAccessConfig = ConfigLoaderBuilder.default()
        .addResourceSource("/secrets-config/secrets-access.conf")
        .build()
        .loadConfigOrThrow<SecretsAccessConfig>()
    val generators = listOf<KeepieGenerator>(RandomStringGenerator())
        .associateBy { it.getName() }

    servicesConfig.print()
    secretsConfig.print()
    secretsAccessConfig.print()

    val keepieService = KeepieService(
        services = servicesConfig.services,
        secrets = secretsConfig.secrets,
        servicesToSecrets = secretsAccessConfig.servicesToSecrets,
        generators = generators
    )
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