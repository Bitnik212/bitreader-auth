package ru.bitreader.auth.repository

import org.eclipse.microprofile.config.inject.ConfigProperty
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.util.TokenUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class AccessTokenRepository {
    companion object {
        const val CONFIG_KEY_ACCESS_DURATION = "ru.bitreader.auth.jwt.access.duration"
        const val CONFIG_KEY_VERIFY_ISSUER = "mp.jwt.verify.issuer"
    }
    private val tokenUtil = TokenUtils

    @ConfigProperty(name = CONFIG_KEY_ACCESS_DURATION)
    private lateinit var tokenDuration: String

    @ConfigProperty(name = CONFIG_KEY_VERIFY_ISSUER)
    private lateinit var issuer: String

    fun create(user: UserModel): String {
        return tokenUtil.generateToken(
            accessTokenId=null,
            userId =user.id!!,
            role = user.role,
            duration=tokenDuration.toLong()*1000,
            issuer=issuer
        )
    }

    val expirationDate: Long
    get() {
        val tokenDuration = tokenDuration.toLong() * 1000
        return  tokenUtil.currentTimeInSecs()+tokenDuration
    }

}