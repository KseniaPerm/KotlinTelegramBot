package ru.ksenia.bot.telegram.api
import kotlinx.serialization.json.Json
import ru.ksenia.bot.trainer.LearnWordsTrainer
import ru.ksenia.bot.trainer.model.Question
import ru.ksenia.bot.telegram.api.entity.InlineKeyboard
import ru.ksenia.bot.telegram.api.entity.ReplyMarkup
import ru.ksenia.bot.telegram.api.entity.SendMessageRequest
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(
    val botToken: String,
    val json: Json,
) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Long): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body().toString()
    }

    fun sendMessage(chatId: Long, message: String): String {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = message,
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body().toString()
    }

    fun sendQuestion(chatId: Long, question: Question): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.correctAnswer.original,
            replyMarkup = ReplyMarkup(
                listOf(
                    question.variants.mapIndexed { index, word ->
                        InlineKeyboard(
                            text = word.translate, callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"

                        )
                    },
                    listOf(
                        InlineKeyboard(
                            text = "Выход",
                            callbackData = MENU
                        )
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        chatId: Long,
    ) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            telegramBotService.sendMessage(chatId, "Все слова выучены")
        } else {
            sendQuestion(chatId, question)
        }
    }

    fun sendMenu(chatId: Long): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(
                            text = "Изучить слова",
                            callbackData = LEARN_WORDS
                        ),
                        InlineKeyboard(
                            text = "Статистика",
                            callbackData = STATISTICS
                        ),
                    ),
                    listOf(
                        InlineKeyboard(
                            text = "Сбросить прогресс",
                            callbackData = RESET_CLICKED
                        )
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)

        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()

        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}

const val LEARN_WORDS = "learn_words_clicked"
const val STATISTICS = "statistics_clicked"
const val RESET_CLICKED = "reset_clicked"
const val MENU = "menu"
const val START = "/start"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"