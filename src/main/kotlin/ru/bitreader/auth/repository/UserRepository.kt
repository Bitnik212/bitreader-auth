package ru.bitreader.auth.repository

import io.quarkus.hibernate.orm.panache.PanacheRepository
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.database.merge
import ru.bitreader.auth.models.database.toUserId
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.models.http.toUserModel
import ru.bitreader.auth.util.PasswordUtil
import ru.bitreader.auth.util.TokenUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class UserRepository: PanacheRepository<UserModel> {
    @Inject
    private lateinit var passwordUtil: PasswordUtil

    private val tokenUtils = TokenUtils

    fun findByUserId(userId: UserId): UserModel? {
        return when (userId.id) {
            null -> when (userId.email) {
                "" -> null
                else -> find("email", userId.email).firstResult<UserModel>()
            }
            else -> find("id", userId.id).firstResult<UserModel>()
        }
    }

    fun findByAccessToken(accessToken: String): UserModel? {
        return if (tokenUtils.isValidToken(accessToken)) {
            findById(tokenUtils.getUserId(accessToken))
        } else null
    }

    @Transactional
    fun create(user: UserModel): Boolean {
        return if (findByUserId(user.toUserId()) == null) try {
            user.password = passwordUtil.encrypt(user.password)
            persist(user.also {
                if (it.username.isNullOrBlank())
                    it.username = it.email?.split("@")?.get(0)
            })
            true
        } catch (e: Exception) {
            println(e)
            false
        } else false
    }

    @Transactional
    fun delete(id: UserId): Boolean {
        val user = findByUserId(id)
        user?.also { delete(user) }?: return false
        return true
    }

    @Transactional
    fun update(user: UserModel): UserModel? {
        var foundedUser = findById(user.id)
        println("${foundedUser?.email} == ${user.email}")
        foundedUser = foundedUser!!.merge(user, passwordUtils = passwordUtil)
        this.entityManager.merge(foundedUser)
        return foundedUser
    }

    @Transactional
    fun suspend(id: UserId): Boolean {
        val user = findByUserId(id)
        return if (user != null) {
            entityManager.merge(user.also { it.suspend = !user.suspend!! })
            true
        } else false
    }

    fun isNotExistEmail(email: String): Boolean {
        if (email.isBlank()) throw IllegalArgumentException("Empty email")
        val result = find("email", email).list<UserModel>()
        return result.size == 0
    }

    fun isNotExistUsername(username: String?): Boolean {
        if (username == null) return false
        if (username.isBlank()) throw IllegalArgumentException("Empty username")
        val result = find("username", username).list<UserModel>()
        return result.size == 0
    }

    /**
     *
     * @param password Пароль пользователя
     * @param encryptedPassword шифрованный пароль из бд
     * **/
    fun isValidPassword(password: String, encryptedPassword: String): Boolean {
        return passwordUtil.isValidPassword(password, encryptedPassword)
    }
}