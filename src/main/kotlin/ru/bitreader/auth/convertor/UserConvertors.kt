package ru.bitreader.auth.convertor

import ru.bitreader.auth.models.database.Role
import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.util.PasswordUtil


fun UserModel.toUserId(): UserId {
    return UserId().also {
        it.id =this.id;
        it.email = this.email?: "";
    }
}

fun UserModel.merge(user: UserModel, changeRole: Boolean = false, passwordUtils: PasswordUtil): UserModel {
    if (user.id != null && this.id != user.id) throw IllegalArgumentException( "user id does not match with argument")
//    if (!user.email.isNullOrBlank() && this.email != user.email) throw IllegalArgumentException( "user email does not match with argument")
    return UserModel().also {
        it.id = if (this.id == null) user.id else this.id;
        it.email = if (this.email.isNullOrBlank() || this.email != user.email) user.email else this.email;
        it.username = if (this.username.isNullOrBlank() || this.username != user.username) user.username else this.username;
        it.password = if (passwordUtils.encrypt(user.password) == this.password) passwordUtils.encrypt(user.password) else this.password
        it.role = if (changeRole) user.role else this.role;
        it.suspend = this.suspend;
    }
}