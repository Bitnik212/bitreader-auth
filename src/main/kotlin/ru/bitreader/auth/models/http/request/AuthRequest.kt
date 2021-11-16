package ru.bitreader.auth.models.http.request

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString
import ru.bitreader.auth.models.http.UserId
import javax.validation.constraints.NotEmpty


@NoArgsConstructor
@AllArgsConstructor
@ToString
class AuthRequest {
    var userId: UserId? = null
    @NotEmpty
    var password: String = ""
}