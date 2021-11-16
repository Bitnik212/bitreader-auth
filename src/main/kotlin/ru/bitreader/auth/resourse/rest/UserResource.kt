package ru.bitreader.auth.resourse.rest

import ru.bitreader.auth.models.database.Role
import ru.bitreader.auth.models.http.request.AuthRequest
import ru.bitreader.auth.models.http.response.SignInResponse
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.models.http.request.SignUpRequest
import ru.bitreader.auth.repository.TokenRepository
import ru.bitreader.auth.repository.UserRepository
import ru.bitreader.auth.util.BaseResponse
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.Valid
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/user")
class UserResource {
    @Inject
    lateinit var userRepository: UserRepository

    @Inject
    lateinit var tokenRepository: TokenRepository

    @PermitAll
    @POST
    @Path("/signin")
    @Produces(MediaType.APPLICATION_JSON)
    fun signIn(@Valid authRequest: AuthRequest): Response {
        return if (authRequest.userId != null) {
            val userId: UserId = authRequest.userId!!
            val user: UserModel? = userRepository.findByUserId(userId)
            if (user != null && userRepository.isValidPassword(authRequest.password, user.password)) {
                BaseResponse.status(data= SignInResponse(token=tokenRepository.create(user).also {
                    it.user?.password = ""
                })).build()
            } else {
                BaseResponse.status(message = "Ошибка авторизации").build()
            }
        } else {
            BaseResponse.unAuthorized()
        }
    }

    @PermitAll
    @POST
    @Path("/signup")
    @Produces(MediaType.APPLICATION_JSON)
    fun signUp(@Valid user: SignUpRequest): Response {
        return if (user.userId != null && user.userId?.email != null) {
            if (
                !user.userId?.email.isNullOrBlank()
                && !userRepository.isNotExistEmail(user.userId!!.email))
                BaseResponse.status(Response.Status.CONFLICT, "Данный email уже занят").build()
            else {
                val newUser = UserModel().also {
                    it.email = user.userId!!.email
                    it.username = user.userId!!.username
                    it.password = user.password
                    it.role = Role.USER
                }
                userRepository.create(newUser)
                BaseResponse.status(data = newUser.also { it.password = "" }).build()
            }
        } else {
            BaseResponse.badRequest()
        }
    }
}