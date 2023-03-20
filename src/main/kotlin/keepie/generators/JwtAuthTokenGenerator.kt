package keepie.generators

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import keepie.Secret
import keepie.config.SecretItem
import keepie.config.ServiceItem
import mu.KotlinLogging
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class JwtAuthTokenGenerator(
    private val keyPair: KeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256),
    private val validFor: Duration = Duration.ofSeconds(10)
) : KeepieGenerator {

    private val logger = KotlinLogging.logger {}

    override fun getName() = "jwt-auth-generator"

    override fun getSecretValue(secretItem: SecretItem, serviceItem: ServiceItem): Secret {
        val expiration = Instant.now().plus(validFor)
        logger.info(
            "Generating token for ${serviceItem.name} that will expire ${
                OffsetDateTime.ofInstant(
                    expiration,
                    ZoneOffset.UTC
                )
            }"
        )
        val jwt = Jwts.builder()
            .setIssuer("keepie-tokens")
            .setSubject(serviceItem.name)
            .setExpiration(Date.from(expiration))
            .signWith(keyPair.private)
            .compact()
        return Secret(secretItem.name, jwt)
    }
}