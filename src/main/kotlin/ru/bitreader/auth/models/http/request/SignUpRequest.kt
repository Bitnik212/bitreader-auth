package ru.bitreader.auth.models.http.request

import ru.bitreader.auth.models.http.UserId
import javax.validation.constraints.NotEmpty

class SignUpRequest {
    var userId: UserId? = null
    @NotEmpty
    var password: String = ""
}