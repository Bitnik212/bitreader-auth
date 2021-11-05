package ru.bitreader.auth.resourse

import ru.bitreader.auth.models.Message
import javax.annotation.security.RolesAllowed
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/resource")
class ResourceREST {
    @RolesAllowed("USER")
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    fun user(): Response {
        return Response.ok(Message("Content for user")).build()
    }

    @RolesAllowed("ADMIN")
    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    fun admin(): Response {
        return Response.ok(Message("Content for admin")).build()
    }

    @RolesAllowed("USER", "ADMIN")
    @GET
    @Path("/user-or-admin")
    @Produces(MediaType.APPLICATION_JSON)
    fun userOrAdmin(): Response {
        return Response.ok(Message("Content for user or admin")).build()
    }
}