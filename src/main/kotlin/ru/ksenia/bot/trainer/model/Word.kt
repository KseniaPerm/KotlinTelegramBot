package ru.ksenia.bot.trainer.model

import kotlinx.serialization.Serializable

@Serializable
data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)