package ru.bitreader.auth.util

import ru.bitreader.auth.models.http.response.BaseResponseModel
import javax.ws.rs.core.*

object BaseResponse {

    fun status(
        status: Response.Status = Response.Status.OK,
        message: String = "Все хорошо",
        data: Any? = null
    ): Response.ResponseBuilder {
        return Response.ok(BaseResponseModel(message, data)).status(status)
    }

    fun unAuthorized(message: String = "Не получилось авторизовать"): Response {
        return status(Response.Status.UNAUTHORIZED, message = message).build()
    }

    fun notFound(): Response {
        return status(Response.Status.NOT_FOUND, "Не нашел").build()
    }

    fun badRequest(): Response {
        return status(Response.Status.BAD_REQUEST, "Не правильный запрос").build()
    }

    fun error(message: String = "Внутряняя ошибка"): Response {
        return status(Response.Status.BAD_GATEWAY, message).build()
    }

}