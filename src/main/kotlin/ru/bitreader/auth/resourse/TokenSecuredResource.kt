package ru.bitreader.auth.resourse

import org.eclipse.microprofile.jwt.JsonWebToken
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.InternalServerErrorException
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.SecurityContext


@Path("/secured")
class TokenSecuredResource {
    @Inject
    var jwt: JsonWebToken? = null

    @GET
    @Path("permit-all")
    @PermitAll
    @Produces(MediaType.TEXT_PLAIN)
    fun hello(@Context ctx: SecurityContext): String? {
        return getResponseString(ctx)
    }

    @GET
    @Path("roles-allowed")
    @RolesAllowed("User", "Admin")
    @Produces(MediaType.TEXT_PLAIN)
    fun helloRolesAllowed(@Context ctx: SecurityContext?): String? {
        return getResponseString(ctx!!).toString() + ", birthdate: " + jwt!!.getClaim<Any>("birthdate").toString()
    }

    private fun getResponseString(ctx: SecurityContext): String? {
        val name: String
        name = if (ctx.userPrincipal == null) {
            "anonymous"
        } else if (ctx.userPrincipal.name != jwt!!.name) {
            throw InternalServerErrorException("Principal and JsonWebToken names do not match")
        } else {
            ctx.userPrincipal.name
        }
        return String.format(
            "hello + %s,"
                    + " isHttps: %s,"
                    + " authScheme: %s,"
                    + " hasJWT: %s",
            name, ctx.isSecure, ctx.authenticationScheme, hasJwt()
        )
    }

    private fun hasJwt(): Boolean {
        return jwt!!.claimNames != null
    }
}