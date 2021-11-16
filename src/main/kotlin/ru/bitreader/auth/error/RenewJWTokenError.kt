package ru.bitreader.auth.error

import org.eclipse.microprofile.graphql.GraphQLException

class RenewJWTokenError(override var message: String = "Ошибка выдочи новых токенов"): GraphQLException()