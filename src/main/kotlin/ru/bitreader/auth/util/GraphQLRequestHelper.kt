package ru.bitreader.auth.util

import org.eclipse.microprofile.jwt.JsonWebToken
import ru.bitreader.auth.error.ValidTokenError
import ru.bitreader.auth.repository.TokenRepository
import javax.inject.Inject

open class GraphQLRequestHelper {
    @Inject
    private lateinit var tokenRepository: TokenRepository

    @Inject
    private lateinit var jwt: JsonWebToken

    @Throws(ValidTokenError::class)
    fun validToken(customToken: String? = null): String {
        val token = customToken ?: (jwt.rawToken?: throw ValidTokenError())
        if (!tokenRepository.isValidAndNotExpiredToken(token))
            throw ValidTokenError("Токен не валидный или истек")
        return token
    }

}