package ru.bitreader.auth.models.database

import org.eclipse.microprofile.graphql.Description
import javax.persistence.*
import javax.validation.constraints.*

@Entity(name = UserModel.TABLE_NAME)
class UserModel {
    companion object {
        const val TABLE_NAME = "users"
    }
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @Description("Генерируетя на основе названия почты. Например в nnv@bitt.moe username будет nnv")
    var username: String? = null
    @Description("Чтобы не менять пароль отправить пустую строку")
    var password: String = ""
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
    @Description("Чтобы не менять почту отправить пустую строку")
    @Email
    @Column(unique = true)
    var email: String? = null
    var suspend: Boolean? = false
    var emailConfirmation: Boolean? = false
}
