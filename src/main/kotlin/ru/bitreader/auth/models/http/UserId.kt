package ru.bitreader.auth.models.http

import org.eclipse.microprofile.graphql.Description
import javax.validation.constraints.NotBlank

class UserId() {
    var id: Long? = null
    @Description
    var username: String? = null
    @NotBlank
    var email: String = ""
}
