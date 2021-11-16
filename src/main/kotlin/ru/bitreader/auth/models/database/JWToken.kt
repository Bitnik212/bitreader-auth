package ru.bitreader.auth.models.database

import java.util.*

class JWToken {
    var access: String = ""
    var accessExpiration: Date? = null
    var refresh: String = ""
    var refreshExpiration: Date? = null
    var user: UserModel? = null
}
