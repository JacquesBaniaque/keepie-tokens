import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import keepie.KeepieService
import keepie.config.SecretsAccessConfig
import keepie.config.SecretsConfig
import keepie.config.ServicesConfig
import keepie.generators.KeyPairGenerator
import keepie.generators.RandomStringGenerator
import mu.KotlinLogging

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
    val generators = listOf(
        RandomStringGenerator(),
        KeyPairGenerator()
    )
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
    val server = Server(
        keepieService = keepieService
    )
    server.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
        logger.info("Server stopped")
    })
}