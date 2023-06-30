package demo.jwt

import io.jsonwebtoken.Jwts
import mu.KotlinLogging
import java.lang.RuntimeException
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class DemoJwtAuthTokenService(
    private val validFor: Duration = Duration.ofSeconds(10)
) {

    private val logger = KotlinLogging.logger {}

    var keyPair: KeyPair? = null
        set(value) {
            logger.info { "Key pair updated" }
            field = value
        }

    fun generateJwtToken(identity: String): String {
        return keyPair?.let { keyPair ->
            val expiration = Instant.now().plus(validFor)
            logger.info(
                "Generating token for $identity that will expire ${
                    OffsetDateTime.ofInstant(
                        expiration,
                        ZoneOffset.UTC
                    )
                }"
            )
            val jwt = Jwts.builder()
                .setIssuer("keepie-tokens")
                .setSubject(identity)
                .setExpiration(Date.from(expiration))
                .signWith(keyPair.private)
                .compact()
            return jwt
        } ?: throw RuntimeException("Not ready yet")
    }

}