package org.example.ru.ksenia.bot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.ksenia.bot.telegram.api.entity.Message

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)