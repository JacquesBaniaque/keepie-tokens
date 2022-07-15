package jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import mu.KotlinLogging
import java.security.KeyPair
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class TokenService(
    private val keyPair: KeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256),
    private val validFor: Duration = Duration.ofSeconds(10)
) {
    private val logger = KotlinLogging.logger {}

    fun createToken(userId: String): String {
        val expiration = Instant.now().plus(validFor)
        logger.info("Generating token for $userId that will expire ${OffsetDateTime.ofInstant(expiration, ZoneOffset.UTC)}")
        return Jwts.builder()
            .setIssuer("keepie-tokens")
            .setSubject(userId)
            .setExpiration(Date.from(expiration))
            .signWith(keyPair.private)
            .compact();
    }
}