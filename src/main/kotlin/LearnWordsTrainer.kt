package org.example

import java.io.File

data class Word(
    val original: String,
    val translate: String,
    var correctAnswerCount: Int = 0,
)

data class Statistics(
    val totalCount: Int,
    val learnedCount: Int,
    val percent: Double,
)


data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer(
    val correctAnswerThreashold: Int = CORRECT_ANSWER,
    val countOfVariants: Int = COUNT_OF_VARIANTS,
) {

    private var question: Question? = null
    private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val totalCount = dictionary.count()
        val learnedCount = dictionary.count { it.correctAnswerCount >= correctAnswerThreashold }
        val percent = ((learnedCount.toDouble() / totalCount.toDouble()) * 100)
        return Statistics(totalCount, learnedCount, percent)

    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswerCount < correctAnswerThreashold }
        if (notLearnedList.isEmpty()) return null
        val variants = notLearnedList.shuffled().take(countOfVariants)
        val correctAnswer = variants.random()
        val questionWords = if (variants.size < countOfVariants) {
            val learnedList = dictionary.filter { it.correctAnswerCount >= correctAnswerThreashold }.shuffled()
            variants + learnedList.take(countOfVariants - notLearnedList.size)
        } else {
            variants
        }.shuffled()


        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer,
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)
            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswerCount++
                saveDictionary(dictionary)
                true
            } else {
                false
            }
        } ?: false

    }

    private fun loadDictionary(): MutableList<Word> {
        try {
            val wordsFile = File("words.txt")
            val lines: List<String> = wordsFile.readLines()
            val dictionary: MutableList<Word> = mutableListOf()
            for (line in lines) {
                val line = line.split("|")
                val word =
                    Word(original = line[0], translate = line[1], correctAnswerCount = line[2].toIntOrNull() ?: 0)
                dictionary.add(word)
            }
            return dictionary
        } catch (e: IndexOutOfBoundsException) {
            throw IllegalStateException("Некорректный файл")
        }
    }

    private fun saveDictionary(words: MutableList<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        for (word in words) {
            wordsFile.appendText("${word.original}|${word.translate}|${word.correctAnswerCount}\n")
        }
    }
}