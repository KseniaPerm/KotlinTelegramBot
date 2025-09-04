package org.example

import kotlin.Int
import kotlin.String

fun Question.asConsoleString(): String {
    val variants = this.variants
        .mapIndexed { index: Int, word: Word -> "${index + 1} - ${word.translate}" }
        .joinToString(separator = "\n")
    return this.correctAnswer.original + "\n" + variants + "\n0 -  выйти в меню"
}

fun main() {

    val trainer = try {
        LearnWordsTrainer()
    } catch (e: Exception) {
        println("Невозможно загрузить словарь")
        return
    }

    while (true) {
        println("Меню:\n1 - Учить слова\n2 - Статистика\n0 - Выход")

        when (readln().toIntOrNull()) {
            1 -> {
                while (true) {
                    val question = trainer.getNextQuestion()
                    if (question == null) {
                        println("Все слова выучены")
                        break
                    } else {
                        println(question.asConsoleString())
                        val userAnswerInput = readln().toIntOrNull()
                        if (userAnswerInput == 0) {
                            println("Выход в меню")
                            println()
                            break
                        }
                        if (trainer.checkAnswer(userAnswerInput?.minus(1))) {
                            println("Правильно\n")
                        } else {
                            println("Неправильно! ${question.correctAnswer.original} - это ${question.correctAnswer.translate}\n")
                        }
                    }
                }
            }

            2 -> {
                val statistics = trainer.getStatistics()
                println(
                    "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${
                        String.format(
                            "%.2f",
                            statistics.percent
                        )
                    }%\n"
                )
            }

            0 -> {
                println("Выход")
                break
            }

            else -> {
                println("Введите число 1, 2 или 0")
            }
        }
    }
}

const val CORRECT_ANSWER = 3
const val NOT_LEARNED_WORDS = 4