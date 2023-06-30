package demo.basic

import keepie.Secret
import mu.KLogging
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path

@Path("/keepie-receiver/service-1")
class DemoBasicReceiverEndpoint {
    companion object: KLogging()

    @POST
    @Consumes("application/json")
    fun receiveSecretService1(secret: Secret) {
        logger.info("Received secret for service-1: ${secret.name}:${secret.value}")
    }

}