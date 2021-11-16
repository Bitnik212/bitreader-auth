package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException


class SignUpError(override val message: String = "Ошибка регистрации!"): GraphQLException() {
    override fun getExceptionType(): ExceptionType {
        return ExceptionType.DataFetchingException
    }
}