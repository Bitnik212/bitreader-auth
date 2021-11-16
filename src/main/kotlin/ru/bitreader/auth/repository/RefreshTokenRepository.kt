package ru.bitreader.auth.repository

import io.quarkus.hibernate.orm.panache.PanacheRepository
import org.eclipse.microprofile.config.inject.ConfigProperty
import ru.bitreader.auth.models.database.ExpiredRefreshTokenModel
import ru.bitreader.auth.models.database.JWToken
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.util.TokenUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.persistence.NoResultException
import javax.transaction.Transactional

@ApplicationScoped
class RefreshTokenRepository: PanacheRepository<ExpiredRefreshTokenModel> {

    companion object {
        const val CONFIG_KEY_VERIFY_ISSUER = "mp.jwt.verify.issuer"
        const val CONFIG_KEY_REFRESH_DURATION = "ru.bitreader.auth.jwt.refresh.duration"
    }

    @ConfigProperty(name = CONFIG_KEY_REFRESH_DURATION)
    private lateinit var refreshTokenDuration: String

    @ConfigProperty(name = CONFIG_KEY_VERIFY_ISSUER)
    private lateinit var issuer: String

    private val tokenUtil = TokenUtils

    fun create(user: UserModel, accessToken: String): String {
        val accessTokenId = accessTokenId(accessToken)!!
        return tokenUtil.generateToken(
            accessTokenId=accessTokenId,
            userId =user.id!!,
            role = user.role,
            duration =refreshTokenDuration.toLong(),
            issuer=issuer
        )
    }

    @Transactional
    fun disable(token: String): Boolean {
        val tokenId = accessTokenId(token, fromRefreshToken = true) ?: return false
        persist(ExpiredRefreshTokenModel().also { it.tokenId = tokenId })
        return true
    }

    fun isDisabled(token: String): Boolean? {
        val tokenId = accessTokenId(token, fromRefreshToken = true) ?: return null
        return try {
            val result = find("tokenId", tokenId).singleResult<ExpiredRefreshTokenModel>() ?: return false
            tokenId == result.tokenId
        } catch (e: NoResultException) {
            false
        }
    }

    val expirationDate: Date
        get() {
            val date = tokenUtil.currentTimeInSecs()+refreshTokenDuration.toLong()
            return Date(date)
        }

    private fun accessTokenId(accessToken: String, fromRefreshToken: Boolean = false): String? {
        val parsed = tokenUtil.decodeTokenPayload(accessToken)
        return if (fromRefreshToken) parsed["accessTokenId"]?.toString() else parsed["jti"]?.toString()
    }

}