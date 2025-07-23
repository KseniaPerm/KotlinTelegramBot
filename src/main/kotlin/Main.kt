package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int? = 0,
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
            }

            2 -> {
                println("Статистика")
            }

            0 -> {
                println("Выход")
                break
            }

            else -> {
                println("Введите число 1, 2 или 0")
            }
        }
        println()
    }
}