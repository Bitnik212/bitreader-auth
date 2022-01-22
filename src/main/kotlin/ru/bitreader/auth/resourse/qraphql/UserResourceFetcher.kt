package ru.bitreader.auth.resourse.qraphql

import org.eclipse.microprofile.graphql.*
import ru.bitreader.auth.error.*
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.database.toUserId
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.models.http.request.SignInRequest
import ru.bitreader.auth.models.http.response.SignInResponse
import ru.bitreader.auth.repository.TokenRepository
import ru.bitreader.auth.repository.UserRepository
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotBlank
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
    fun signUp(@Valid user: UserModel): SignInResponse {
        return if (repository.create(user)) {
            SignInResponse(tokenRepository.create(user.also { it.password = "" }))
        } else {
                throw SignUpError("Такой пользователь уже есть")
            }
    }

    @Throws(SignInError::class, ConstraintViolationException::class)
    @PermitAll
    @Query("signIn")
    @Description("Авторизация пользователя")
    fun signIn(@Valid user: SignInRequest): SignInResponse {
        val foundedUser = repository.findByUserId(user.userId!!)
        return if (foundedUser != null ) {
            if (!repository.isValidPassword(user.password, foundedUser.password))
                throw SignInError("Authorization Error")
            val token = tokenRepository.create(foundedUser)
            SignInResponse(token)
        } else throw SignInError("User not found")
    }

    @Throws(UpdateUserError::class, ValidTokenError::class, ConstraintViolationException::class, TokenOwnershipError::class)
    @RolesAllowed("USER")
    @Mutation("updateUser")
    @Description("Изменить поля пользователя. Обязательно указывать id пользователя, чтобы найти пользователя")
    fun update(@Valid user: UserModel, @Valid @NotBlank accessToken: String): UserModel {
        if (!tokenRepository.isValidAndNotExpiredToken(accessToken))
            throw ValidTokenError("Токен не валидный или истек")
        if (tokenRepository.tokenOwnedByUserId(accessToken, user.toUserId())) {
            return repository.update(user)?: throw UpdateUserError()
        }
        else throw TokenOwnershipError()
    }

    @Throws(ValidTokenError::class, ConstraintViolationException::class)
    @RolesAllowed("USER")
    @Mutation("deleteUser")
    @Description("Удаления пользователя")
    fun delete(@Valid @NotBlank accessToken: String): Boolean {
        if (!tokenRepository.isValidAndNotExpiredToken(accessToken))
            throw ValidTokenError("Токен не валидный или истек")
        return repository.delete(id = UserId().also {
            it.id = tokenRepository.tokenUtil.getUserId(accessToken)
        })
    }

}