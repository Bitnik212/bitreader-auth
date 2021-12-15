package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException

class UpdateUserError(
    override val message: String? = "Ошибка изменения данных пользователя. Возможно пользователь не найден"
): GraphQLException() {
}