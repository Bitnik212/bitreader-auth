package ru.bitreader.auth.models.database

import javax.persistence.*
import javax.validation.constraints.*

@Entity(name = "users")
class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @Null
    var username: String? = null
    @NotEmpty
    var password: String = ""
    @Enumerated(EnumType.STRING)
    var role: Role = Role.USER
    @Email
    @NotEmpty
    @Column(unique = true)
    var email: String? = null
    var suspend: Boolean = false
}
