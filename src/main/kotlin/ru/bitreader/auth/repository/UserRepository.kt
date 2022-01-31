package ru.bitreader.auth.repository

import io.quarkus.hibernate.orm.panache.PanacheRepository
import ru.bitreader.auth.convertor.merge
import ru.bitreader.auth.convertor.toUserId
import ru.bitreader.auth.models.database.Role
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
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

    /**
     * Не кидать не существующего пользоваетля!!!!
     * **/
    @Transactional
    fun update(user: UserModel, role: Role? = null): UserModel? {
        var foundUser = findByUserId(user.toUserId())?: return null
        foundUser = user.merge(originalUser = foundUser, passwordUtils = passwordUtil, changeRole = false)
        return entityManager.merge(foundUser)
    }

    fun isNotExistEmail(email: String): Boolean {
        if (email.isBlank()) throw IllegalArgumentException("Empty email")
        val result = find("email", email).list<UserModel>()
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