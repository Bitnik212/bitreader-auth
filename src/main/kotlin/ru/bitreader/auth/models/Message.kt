package ru.bitreader.auth.models

import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.ToString


@NoArgsConstructor
@AllArgsConstructor
@ToString
data class Message (
    var content: String? = null
)