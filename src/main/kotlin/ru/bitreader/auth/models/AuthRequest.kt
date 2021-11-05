package ru.bitreader.auth.models

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString


@NoArgsConstructor
@AllArgsConstructor
@ToString
class AuthRequest {
    var username: String? = null
    var password: String? = null
}