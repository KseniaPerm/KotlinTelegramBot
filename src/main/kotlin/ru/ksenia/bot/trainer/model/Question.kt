package ru.ksenia.bot.trainer.model

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)