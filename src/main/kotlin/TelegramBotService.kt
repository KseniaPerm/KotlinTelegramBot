package org.example

import kotlinx.serialization.json.Json
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

    fun sendMessage(botToken: String, chatId: Long?, message: String): String {
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

    fun sendQuestion(json: Json, chatId: Long?, question: Question): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val keyboard = question.variants
            .mapIndexed { index: Int, word: Word ->
                """{ "text": "${word.translate}", "callback_data": "${CALLBACK_DATA_ANSWER_PREFIX}$index"}"""
            }
            .joinToString(separator = ",", prefix = "[", postfix = "]")
        val exit = """{"text": "Выход", "callback_data": "$MENU"}"""
        val keyboardWithExit = """
           [  
               $keyboard, 
               [$exit]
           ]
        """.trimIndent()

        val sendMenuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Выбрать правильный перевод: ${question.correctAnswer.original}",
            	"reply_markup": {
            		"inline_keyboard":  $keyboardWithExit
            	}
            }
        """.trimIndent()

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
                    listOf(InlineKeyboard(text = "Выход", callbackData = MENU))
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
        json: Json,
        trainer: LearnWordsTrainer,
        telegramBotService: TelegramBotService,
        chatId: Long?,
    ) {
        val question = trainer.getNextQuestion()
        if (question == null) {
            telegramBotService.sendMessage(botToken, chatId, "Все слова выучены")
        } else {
            sendQuestion(json, chatId, question)
        }
    }

    fun sendMenu(json: Json, botToken: String, chatId: Long?): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = "Изучить слова", callbackData = LEARN_WORDS),
                        InlineKeyboard(text = "Статистика", callbackData = STATISTICS),
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
const val MENU = "menu"
const val START = "/start"
const val CALLBACK_DATA_ANSWER_PREFIX = "answer_"