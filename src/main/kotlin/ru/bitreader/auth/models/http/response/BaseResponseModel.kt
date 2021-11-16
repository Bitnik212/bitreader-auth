package ru.bitreader.auth.models.http.response

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString

//@NoArgsConstructor
//@AllArgsConstructor
//@ToString
data class BaseResponseModel(
    var message: String = "Все ок",
    var data: Any?
)
