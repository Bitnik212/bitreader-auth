package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException
import java.lang.RuntimeException

class SignInError(override var message: String = "Ошибка авторизации"): GraphQLException() {
}