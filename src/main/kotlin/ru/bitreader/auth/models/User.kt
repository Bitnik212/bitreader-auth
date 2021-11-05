package ru.bitreader.auth.models

import lombok.AllArgsConstructor
import lombok.EqualsAndHashCode
import lombok.NoArgsConstructor
import lombok.ToString


@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
data class User (var username: String, var password: String, var roles: Set<Role>) {
//    var username: String? = null

    companion object {
        // this is just an example, you can load the user from the database (via PanacheEntityBase)
        fun findByUsername(username: String): User? {

            //if using Panache pattern (extends or PanacheEntity PanacheEntityBase)
            //return find("username", username).firstResult();
            val data: MutableMap<String, User> = HashMap()

            //username:passwowrd -> user:user
            data["user"] = User("user", "cBrlgyL2GI2GINuLUUwgojITuIufFycpLG4490dhGtY=", setOf(Role.USER))
            //username:passwowrd -> admin:admin
            data["admin"] = User("admin", "dQNjUIMorJb8Ubj2+wVGYp6eAeYkdekqAcnYp+aRq5w=", setOf(Role.ADMIN))
            return if (data.containsKey(username)) {
                data[username]
            } else {
                null
            }
        }
    }

}