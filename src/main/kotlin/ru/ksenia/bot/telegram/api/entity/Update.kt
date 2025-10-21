package org.example.ru.ksenia.bot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.ksenia.bot.telegram.api.entity.Message

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)