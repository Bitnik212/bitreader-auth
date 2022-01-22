package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException

class ValidTokenError(
    override val message: String? = "Ошибка валидации токена. Возможно токен поврезжден"
): GraphQLException() {
}