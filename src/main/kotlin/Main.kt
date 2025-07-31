package org.example

import java.io.File
import kotlin.Int
import kotlin.String

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

fun loadDictionary(): MutableList<Word> {
    val wordsFile = File("words.txt")
    val lines: List<String> = wordsFile.readLines()
    val dictionary: MutableList<Word> = mutableListOf()
    for (line in lines) {
        val line = line.split("|")
        val word = Word(original = line[0], translate = line[1], correctAnswerCount = line[2].toIntOrNull() ?: 0)
        dictionary.add(word)
    }
    return dictionary
}

fun main() {
    val dictionary = loadDictionary()
    while (true) {
        println("Введите число:\n1 - Учить слова\n2 - Статистика\n0 - Выход")
        val number = readln().toInt()
        println()

        when (number) {
            1 -> {
                println("Учить слова")
                val notLearnedList = dictionary.filter { it.correctAnswerCount < CORRECT_ANSWER }

                if (notLearnedList.isEmpty()) {
                    println("Все слова выучены")
                    continue
                }
                val questionWords = notLearnedList.take(4).shuffled()
                val correctAnswer = questionWords.random()
                println()
                println("${correctAnswer.original}:")
                println(questionWords.forEachIndexed { index, answers -> println("${index + 1}.${answers.translate}") })
                println()
            }

            2 -> {
                println("Статистика")
                val totalCount = dictionary.count()
                println("Количество слов в словаре: $totalCount")
                val learnedCount = dictionary.count { it.correctAnswerCount >= CORRECT_ANSWER }
                val percent = ((learnedCount.toDouble() / totalCount.toDouble()) * 100)
                println("Выучено $learnedCount из $totalCount слов | ${String.format("%.2f", percent)}%\n")
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