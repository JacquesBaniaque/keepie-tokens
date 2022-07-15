package api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces

@Path("/health")
class HealthEndpoint {

    @GET
    @Produces("application/json")
    fun health(): Health {
        return Health(true)
    }

    data class Health(val healthy: Boolean)
}
