package ru.bitreader.auth.convertor

import ru.bitreader.auth.models.database.UserModel
import ru.bitreader.auth.models.http.UserId
import ru.bitreader.auth.util.PasswordUtil


fun UserModel.toUserId(): UserId {
    return UserId().also {
        it.id =this.id;
        it.email = this.email?: "";
    }
}

/**
 * Слить данные пользователя.
 * @param originalUser оригинальный пользователь
 * @param UserModel изменяемые данные
 * **/
fun UserModel.merge(originalUser: UserModel, changeRole: Boolean = false, passwordUtils: PasswordUtil): UserModel {
    if (this.id != null && this.id != originalUser.id) throw IllegalArgumentException( "user id does not match with argument")
    return UserModel().also {
        it.id = originalUser.id
        it.email = if (!this.email.isNullOrBlank() && this.email != originalUser.email) this.email else originalUser.email;
        it.username = if (!this.username.isNullOrBlank() && this.username != originalUser.username)
            this.username else originalUser.username;
        it.password = if (passwordUtils.encrypt(this.password) == originalUser.password)
            originalUser.password else passwordUtils.encrypt(this.password)
        it.role = if (changeRole) this.role else originalUser.role;
        it.suspend = this.suspend;
    }
}