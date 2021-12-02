package ru.bitreader.auth.models.database

import net.bytebuddy.implementation.bind.annotation.Empty
import javax.persistence.*
import javax.validation.constraints.*

@Entity(name = "users")
class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    var username: String? = null
    @NotEmpty
    var password: String = ""
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
    @Email
    @NotEmpty
    @Column(unique = true)
    var email: String? = null
    var suspend: Boolean? = false
}
