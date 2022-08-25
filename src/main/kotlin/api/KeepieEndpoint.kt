package api

import keepie.KeepieService
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Response

@Path("/keepie/{secretName}")
class KeepieEndpoint(private val keepieService: KeepieService) {
    @GET
    @Produces("text/plain")
    fun requestSecret(
        @PathParam("secretName") secretName: String,
        @QueryParam("reply-to-url") replyToUrl: String,
    ): Response {
        keepieService.sendSecret(secretName, replyToUrl)
        return Response.ok().entity("OK").build()
    }
}