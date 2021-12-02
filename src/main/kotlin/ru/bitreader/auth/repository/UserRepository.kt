package ru.bitreader.auth.repository

import io.quarkus.hibernate.orm.panache.PanacheRepository
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.util.PasswordUtil
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional

@ApplicationScoped
class UserRepository: PanacheRepository<UserModel> {
    @Inject
    private lateinit var passwordUtil: PasswordUtil

    fun findByUserId(userId: UserId): UserModel? {
        return when (userId.id) {
            null -> when (userId.email) {
                "" -> when (userId.username) {
                    null -> null
                    else -> find("username", userId.username).firstResult<UserModel>()
                }
                else -> find("email", userId.email).firstResult<UserModel>()
            }
            else -> find("id", userId.id).firstResult<UserModel>()
        }
    }

    @Transactional
    fun create(user: UserModel): Boolean {
        val userId = UserId().also {
            it.username = user.username;
            it.email = user.email?: ""
        }
        return if (findByUserId(userId) == null) try {
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
    fun delete(id: UserId) {
        val user = findByUserId(id)
        delete(user)
    }

    @Transactional
    fun suspend(id: UserId): Boolean {
        val user = findByUserId(id)
        return if (user != null) {
            update("suspend")
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