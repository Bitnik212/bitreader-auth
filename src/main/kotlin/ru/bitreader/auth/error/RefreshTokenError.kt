package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException

class RefreshTokenError(override val message: String? = "Ошибка refresh токена"): GraphQLException() {
}