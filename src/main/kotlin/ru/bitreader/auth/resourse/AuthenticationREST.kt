package ru.bitreader.auth.resourse

import org.eclipse.microprofile.config.inject.ConfigProperty
import ru.bitreader.auth.util.TokenUtils.generateToken
import ru.bitreader.auth.models.AuthRequest
import ru.bitreader.auth.models.AuthResponse
import ru.bitreader.auth.models.User
import ru.bitreader.auth.util.PBKDF2Encoder
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/auth")
class AuthenticationREST {
    @Inject
    lateinit var passwordEncoder: PBKDF2Encoder

    @ConfigProperty(name = "ru.bitreader.auth.jwt.duration")
    lateinit var duration: String

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    lateinit var issuer: String

    @PermitAll
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    fun login(authRequest: AuthRequest): Response {
        val u: User? = User.findByUsername(authRequest.username!!)
        return if (u != null && u.password.equals(passwordEncoder.encode(authRequest.password!!))) {
            try {
                val authResponse = AuthResponse(generateToken(u.username, u.roles, duration.toLong(), issuer))
                Response.ok(authResponse).build()
            } catch (e: Exception) {
                Response.status(Response.Status.UNAUTHORIZED).build()
            }
        } else {
            Response.status(Response.Status.UNAUTHORIZED).build()
        }
    }
}