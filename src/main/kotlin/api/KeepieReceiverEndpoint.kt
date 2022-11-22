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
    @Path("/service-1")
    fun receiveSecretService1(secret: Secret) {
        logger.info("Received secret for service-1: ${secret.name}:${secret.value}")
    }

    @POST
    @Consumes("application/json")
    @Path("/service-2")
    fun receiveSecretService2(secret: Secret) {
        logger.info("Received secret service-2: ${secret.name}:${secret.value}")
    }
}