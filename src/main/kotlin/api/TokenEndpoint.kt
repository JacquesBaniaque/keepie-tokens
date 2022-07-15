package api

import jwt.TokenService
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces

@Path("token")
class TokenEndpoint(private val tokenService: TokenService) {
    @GET
    @Path("/{userId}")
    @Produces("application/json")
    fun token(@PathParam("userId") userId: String): String {
        return tokenService.createToken(userId)
    }
}