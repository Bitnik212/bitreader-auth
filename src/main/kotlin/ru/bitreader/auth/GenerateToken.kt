package ru.bitreader.auth

import io.smallrye.jwt.build.Jwt
import org.eclipse.microprofile.jwt.Claims
import java.util.*
import kotlin.collections.HashSet

//
//object GenerateToken {
//    /**
//     * Generate JWT token
//     */
//    @JvmStatic
//    fun main(args: Array<String>) {
//        val token: String = Jwt.issuer("https://example.com/issuer")
//            .upn("jdoe@quarkus.io")
//            .groups(HashSet<Any?>(Arrays.asList("User", "Admin")))
//            .claim(Claims.birthdate.name, "2001-07-13")
//            .sign()
//        println(token)
//    }
//}
