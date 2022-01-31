package ru.bitreader.auth.resourse.qraphql

import org.eclipse.microprofile.graphql.Description
import org.eclipse.microprofile.graphql.GraphQLApi
import org.eclipse.microprofile.graphql.Mutation
import org.eclipse.microprofile.graphql.Query
import ru.bitreader.auth.convertor.toUserId
import ru.bitreader.auth.error.*
import ru.bitreader.auth.models.database.Role
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.models.http.response.SignInResponse
import ru.bitreader.auth.repository.EmailRepository
import ru.bitreader.auth.repository.TokenRepository
import ru.bitreader.auth.repository.UserRepository
import ru.bitreader.auth.util.GraphQLRequestHelper
import javax.annotation.security.PermitAll
import javax.annotation.security.RolesAllowed
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Valid

@GraphQLApi
class UserResourceFetcher: GraphQLRequestHelper() {

    companion object {
        const val USER_ROLE = "USER"
        const val ADMIN_ROLE = "ADMIN"
        const val SUFFIX = "User"
    }

    @Inject
    private lateinit var tokenRepository: TokenRepository

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var emailRepository: EmailRepository

    @Description("Регистрация пользователя. Обязательные поля: email, password")
    @PermitAll
    @Throws(SignUpError::class, ValidationExceptionHandleError::class)
    @Mutation
    fun signUp(@Valid user: UserModel): SignInResponse {
        if (user.password.isEmpty())
            throw ValidationExceptionHandleError(ConstraintViolationException("Пароль не должен быть пустым", null))
        return if (userRepository.create(user)) {
            emailRepository.sendWelcome(user)
            SignInResponse(tokenRepository.create(user.also { it.password = "" }))
        } else {
                throw SignUpError("Такой пользователь уже есть")
            }
    }

    @Throws(SignInError::class, ConstraintViolationException::class)
    @PermitAll
    @Query
    @Description("Авторизация пользователя. Обязательные поля: email, password")
    fun signIn(@Valid user: UserModel): SignInResponse {
        val foundedUser = userRepository.findByUserId(user.toUserId())
        return if (foundedUser != null ) {
            if (!userRepository.isValidPassword(user.password, foundedUser.password))
                throw SignInError()
            val token = tokenRepository.create(foundedUser)
            SignInResponse(token)
        } else throw SignInError("Пользователь не найден")
    }

    @Throws(UpdateUserError::class, ValidTokenError::class, ConstraintViolationException::class, TokenOwnershipError::class)
    @RolesAllowed(USER_ROLE, ADMIN_ROLE)
    @Mutation("update$SUFFIX")
    @Description("Изменить поля пользователя. Обязательные поля: id. Нельзя изменить роль.")
    fun update(@Valid user: UserModel): UserModel {
        val token = validToken()
        if (tokenRepository.tokenOwnedByUserId(token, user.toUserId())) {
            return userRepository.update(
                user=user,
                role=Role.valueOf(tokenRepository.tokenUtil.decodeTokenPayload(token)["role"].toString()))
                .also { it?.password = "" }?: throw UpdateUserError()
        }
        else throw TokenOwnershipError()
    }

    @Throws(ValidTokenError::class, ConstraintViolationException::class)
    @RolesAllowed(USER_ROLE, ADMIN_ROLE)
    @Mutation("delete$SUFFIX")
    @Description("Удаления пользователя")
    fun delete(): Boolean {
        return userRepository.delete(id = UserId().also {
            it.id = tokenRepository.tokenUtil.getUserId(validToken())
        })
    }

}