package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int? = 0,
)

fun main() {
    val wordsFile = File("words.txt")
    val lines: List<String> = wordsFile.readLines()
    val dictionary: MutableList<Word> = mutableListOf()

    for (line in lines) {
        val line = line.split("|")
        val word = Word(original = line[0], translate = line[1], correctAnswerCount = line[2].toIntOrNull() ?: 0)
        dictionary.add(word)
        println(word)
        Thread.sleep(1000)
    }
}