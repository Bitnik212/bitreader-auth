package ru.bitreader.auth.resourse.rest

import ru.bitreader.auth.repository.AccessTokenRepository
import ru.bitreader.auth.repository.TokenRepository
import ru.bitreader.auth.util.BaseResponse
import java.nio.charset.StandardCharsets
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/token")
class TokenResource {

    @Inject
    lateinit var tokenRepository: TokenRepository

    @PermitAll
    @POST
    @Path("/refresh")
    @Produces(MediaType.APPLICATION_JSON)
    fun refresh(@Valid @NotBlank refreshToken: String): Response {
        val jwToken = tokenRepository.renew(refreshToken)
        return if (jwToken == null) BaseResponse.badRequest()
        else BaseResponse.status(data = jwToken).build()
    }

    @PermitAll
    @GET
    @Path("/publicKey")
    @Produces(MediaType.APPLICATION_JSON)
    fun publicKey(): Response {
        val privateKey = tokenRepository.publicKey
        println(privateKey)
        return BaseResponse.status(data = privateKey).build()
    }
}