import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jwt.TokenService
import mu.KotlinLogging
import java.time.Duration

private val logger = KotlinLogging.logger {}
fun main(args: Array<String>) {
    val tokenService = TokenService(
        Keys.keyPairFor(SignatureAlgorithm.RS256),
        Duration.ofSeconds(10)
    )
    val server = Server(
        tokenService = tokenService
    )
    server.start()

    Runtime.getRuntime().addShutdownHook(Thread {
        server.stop()
        logger.info("Server stopped")
    })
}