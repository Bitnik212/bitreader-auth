package ru.bitreader.auth.models.http.response

data class BaseResponseModel(
    var message: String = "Все ок",
    var data: Any?
)
