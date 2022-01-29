package ru.bitreader.auth.models.http.response

import ru.bitreader.auth.models.database.JWTokenPair


data class SignInResponse (
    var token: JWTokenPair? = null,
)