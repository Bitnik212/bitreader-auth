package ru.bitreader.auth.models.database


class JWTokenPair {
    var access: String = ""
    var accessExpiration: Long? = null
    var refresh: String = ""
    var refreshExpiration: Long? = null
    var user: UserModel? = null
}
