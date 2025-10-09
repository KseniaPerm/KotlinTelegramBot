package org.example.ru.ksenia.bot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)