package api

import keepie.Secret
import mu.KLogger
import mu.KLogging
import mu.KotlinLogging
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/keepie-receiver")
class KeepieReceiverEndpoint {
    companion object: KLogging()

    @POST
    @Consumes("application/json")
    fun receiveSecret(secret: Secret) {
        logger.info("Received secret ${secret.name}:${secret.value}")
    }
}