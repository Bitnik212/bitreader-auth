package ru.bitreader.auth.models.http.response

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString
import ru.bitreader.auth.models.database.JWTokenPair


@NoArgsConstructor
@AllArgsConstructor
@ToString
data class SignInResponse (
    var token: JWTokenPair? = null,
)