package ru.bitreader.auth.models.http

import org.eclipse.microprofile.graphql.Description
import ru.bitreader.auth.models.database.UserModel
import javax.validation.constraints.NotBlank

class UserId() {
    var id: Long? = null
//    @Description для будущих функций
//    var username: String? = null
    @NotBlank
    var email: String = ""
}

fun UserId.toUserModel(user: UserModel): UserModel {
    return UserModel().also {
        it.id = if (this.id == null ) user.id else this.id;
        it.email = if (this.email.isBlank()) user.email else this.email;
        it.username = user.username;
        it.password = user.password;
        it.role = user.role;
        it.suspend = user.suspend;
    }
}
