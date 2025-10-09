package ru.ksenia.bot.telegram

import kotlinx.serialization.json.Json
import org.example.ru.ksenia.bot.Response
import org.example.ru.ksenia.bot.Update
import ru.ksenia.bot.telegram.api.CALLBACK_DATA_ANSWER_PREFIX
import ru.ksenia.bot.telegram.api.LEARN_WORDS
import ru.ksenia.bot.telegram.api.MENU
import ru.ksenia.bot.telegram.api.RESET_CLICKED
import ru.ksenia.bot.telegram.api.START
import ru.ksenia.bot.telegram.api.STATISTICS
import ru.ksenia.bot.telegram.api.TelegramBotService
import ru.ksenia.bot.trainer.LearnWordsTrainer

fun main(args: Array<String>) {

    val botToken = args[0]
    var lastUpdateId = 0L
    val telegramBotService = TelegramBotService(botToken, Json)

    val json = Json { ignoreUnknownKeys = true }
    val trainers = HashMap<Long, LearnWordsTrainer>()

    while (true) {
        Thread.sleep(2000)
        val responseString: String = telegramBotService.getUpdates(lastUpdateId)
        println(responseString)
        val response: Response = json.decodeFromString(responseString)
        if (response.result.isEmpty()) continue
        val sortedUpdates = response.result.sortedBy { it.updateId }
        sortedUpdates.forEach { handleUpdate(it, trainers, telegramBotService) }
        lastUpdateId = sortedUpdates.last().updateId + 1
    }
}

fun handleUpdate(
    update: Update,
    trainers: HashMap<Long, LearnWordsTrainer>,
    telegramBotService: TelegramBotService,
) {

    val message = update.message?.text
    println(message)

    val chatId = update.message?.chat?.id
        ?: update.callbackQuery?.message?.chat?.id ?: return

    val data = update.callbackQuery?.data
    val trainer = trainers.getOrPut(chatId) { LearnWordsTrainer("$chatId.txt") }

    when {
        (message?.lowercase() == MENU) || ((message == START)) -> {
            telegramBotService.sendMenu(chatId)
        }

        data?.lowercase() == STATISTICS -> {
            val trainerStat = trainer.getStatistics()
            telegramBotService.sendMessage(
                chatId, "Всего слов: ${trainerStat.totalCount}, Выучено: ${trainerStat.learnedCount}," +
                        " Статистика: ${trainerStat.percent}"
            )
        }

        data?.lowercase() == RESET_CLICKED -> {
            trainer.resetProgress()
            telegramBotService.sendMessage(chatId, "Прогресс сброшен")
        }

        data?.lowercase() == LEARN_WORDS -> {
            telegramBotService.checkNextQuestionAndSend(trainer, telegramBotService, chatId)

        }

        data?.lowercase() == MENU -> {
            telegramBotService.sendMenu(chatId)
        }

        data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) ?: false -> {

            val userAnswerIndex = data.removePrefix(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()
            val lastQuestion = trainer.question

            val isCorrect = trainer.checkAnswer(userAnswerIndex)

            if (isCorrect) {
                telegramBotService.sendMessage(chatId, "Правильно!")
            } else {
                val wordToTranslate = lastQuestion?.correctAnswer?.original
                val correctAnswer = lastQuestion?.correctAnswer?.translate
                telegramBotService.sendMessage(
                    chatId,
                    "Неправильно! $wordToTranslate - это $correctAnswer"
                )
            }
            telegramBotService.checkNextQuestionAndSend(trainer, telegramBotService, chatId)
        }
    }
}