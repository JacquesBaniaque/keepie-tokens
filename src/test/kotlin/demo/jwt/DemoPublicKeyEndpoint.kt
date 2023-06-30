package demo.jwt

import java.security.PublicKey
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/keepie/public-keys")
class DemoPublicKeyEndpoint(private val jwtAuthTokenService: DemoJwtAuthTokenService) {

    private val publicKey: PublicKey?
        get() = jwtAuthTokenService.keyPair?.public

    @GET
    @Produces("application/json")
    fun requestSecret(): String {
        return publicKey?.encoded?.toString() ?: throw RuntimeException("No key available")
    }
}