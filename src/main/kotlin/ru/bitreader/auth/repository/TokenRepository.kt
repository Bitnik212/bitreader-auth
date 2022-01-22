package ru.bitreader.auth.repository

import io.jsonwebtoken.MalformedJwtException
import ru.bitreader.auth.models.database.JWTokenPair
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.util.TokenUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class TokenRepository {

    @Inject
    private lateinit var userRepository: UserRepository

    @Inject
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Inject
    private lateinit var accessTokenRepository: AccessTokenRepository

    val tokenUtil = TokenUtils
    val publicKey = tokenUtil.getPublicKeyByString()

    fun create(user: UserModel): JWTokenPair {
        val accessToken = accessTokenRepository.create(user)
        val token = JWTokenPair()
            .also {
                it.refresh = refreshTokenRepository.create(user, accessToken)
                it.access = accessToken
                it.accessExpiration = accessTokenRepository.expirationDate
                it.refreshExpiration = refreshTokenRepository.expirationDate
                it.user = user
            }
        return token.also { it.user?.password = "" }
    }

    fun renew(refreshToken: String): JWTokenPair? {
        if (isExpired(refreshToken)) return null
        if (refreshTokenRepository.isDisabled(refreshToken) != false) return null
        val userId = findUserIdInToken(token = refreshToken)?: return null
        val user = userRepository.findByUserId(userId = UserId().also { it.id = userId.toLong() })
        var jwTokenPair: JWTokenPair? = null
        if (tokenUtil.isValidToken(refreshToken)) {
            user?.also { jwTokenPair = create(it) }
            refreshTokenRepository.disable(refreshToken)
        }
        return jwTokenPair
    }

    fun tokenOwnedByUserId(accessToken: String, userId: UserId): Boolean {
        val foundedUser = userRepository.findByUserId(userId)
        val result = foundedUser == userRepository.findByAccessToken(accessToken)
        return if (foundedUser == null) false
        else result
    }

    private fun isExpired(token: String): Boolean {
        val parsed = tokenUtil.decodeTokenPayload(token)
        val nowDate: Long = tokenUtil.currentTimeInSecs()
        val dateLong = parsed["exp"]?: return false
        val tokenDate = Date(dateLong.toString().toLong()).toInstant().toEpochMilli()
        return tokenDate < nowDate
    }

    fun isValidAndNotExpiredToken(token: String): Boolean {
        return try {
            tokenUtil.isValidToken(token) && !isExpired(token)
        } catch (e: MalformedJwtException) {
            false
        }
    }

    private fun findUserIdInToken(token: String): Int? {
        val parsed = tokenUtil.decodeTokenPayload(token)
        val userId = parsed["sub"]?: return null
        return userId.toString().toInt()
    }

}