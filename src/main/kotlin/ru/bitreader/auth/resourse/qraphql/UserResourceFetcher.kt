package ru.bitreader.auth.resourse.qraphql

import org.eclipse.microprofile.graphql.Description
import org.eclipse.microprofile.graphql.GraphQLApi
import org.eclipse.microprofile.graphql.Mutation
import org.eclipse.microprofile.graphql.Query
import ru.bitreader.auth.error.SignInError
import ru.bitreader.auth.error.SignUpError
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.request.AuthRequest
import ru.bitreader.auth.models.http.response.SignInResponse
import ru.bitreader.auth.repository.TokenRepository
import ru.bitreader.auth.repository.UserRepository
import javax.annotation.security.PermitAll
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import kotlin.jvm.Throws

@GraphQLApi
class UserResourceFetcher {
    @Inject
    lateinit var tokenRepository: TokenRepository

    @Inject
    private lateinit var repository: UserRepository

    @Description("Регистрация пользователя")
    @PermitAll
    @Throws(SignUpError::class, ConstraintViolationException::class)
    @Mutation
    fun signUp(@Valid user: UserModel): UserModel {
        return if (repository.create(user)) user.also { it.password = "" }
            else {
                throw SignUpError("Такой пользователь уже есть")
            }
    }

    @Throws(SignInError::class, ConstraintViolationException::class)
    @PermitAll
    @Query("signIn")
    @Description("Авторизация пользователя")
    fun signIn(@Valid user: AuthRequest): SignInResponse {
        val foundedUser = repository.findByUserId(user.userId!!)
        return if (foundedUser != null ) {
            if (!repository.isValidPassword(user.password, foundedUser.password))
                throw SignInError("Authorization Error")
            val token = tokenRepository.create(foundedUser)
            SignInResponse(token)
        } else throw SignInError("User not found")
    }

}