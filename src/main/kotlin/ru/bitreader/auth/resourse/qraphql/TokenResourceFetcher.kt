package ru.bitreader.auth.resourse.qraphql

import org.eclipse.microprofile.graphql.Description
import org.eclipse.microprofile.graphql.GraphQLApi
import org.eclipse.microprofile.graphql.Mutation
import org.eclipse.microprofile.graphql.Query
import ru.bitreader.auth.error.RefreshTokenError
import ru.bitreader.auth.error.RenewJWTokenError
import ru.bitreader.auth.models.database.JWTokenPair
import ru.bitreader.auth.repository.TokenRepository
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import kotlin.jvm.Throws

@GraphQLApi
class TokenResourceFetcher {
    @Inject
    private lateinit var tokenRepository: TokenRepository

    @Throws(RenewJWTokenError::class, ConstraintViolationException::class, RefreshTokenError::class)
    @Description("Получить новый токены access и refresh")
    @Mutation
    fun renew(@Valid @NotBlank refreshToken: String): JWTokenPair {
        return tokenRepository.renew(refreshToken) ?: throw RenewJWTokenError()
    }

    @Description("Получить публичный ключь")
    @Query
    fun publicKey(): String {
        return tokenRepository.publicKey
    }

}