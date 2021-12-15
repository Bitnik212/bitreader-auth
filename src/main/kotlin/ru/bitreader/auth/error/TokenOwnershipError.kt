package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException

class TokenOwnershipError(
    override val message: String? = "Возможно такого пользователя нет или этот токен не указонного пользователя"
): GraphQLException() {
}