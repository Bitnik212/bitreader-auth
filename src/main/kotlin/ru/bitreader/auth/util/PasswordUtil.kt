package ru.bitreader.auth.util

import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class PasswordUtil {
    @Inject
    private lateinit var passwordEncoder: PBKDF2Encoder

    fun encrypt(password: String): String {
        return passwordEncoder.encode(password)
    }

    fun isValidPassword(password: String, encryptedPassword: String): Boolean {
        val e = encrypt(password)
        return e == encryptedPassword
    }

}