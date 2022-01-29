package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException
import javax.validation.ValidationException

class ValidationExceptionHandleError(override var message: String? = "Ошибка валидации"): GraphQLException() {
    constructor(validationException: ValidationException) : this() {
        message = validationException.message
    }
}