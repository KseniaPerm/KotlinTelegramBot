package org.example.ru.ksenia.bot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
   // @SerialName("result")
    val ok: Boolean,
    val result: List<Update>? = null,
    val description: String? = null,
)