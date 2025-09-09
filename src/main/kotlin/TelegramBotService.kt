package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(
    val botToken: String,
) {

    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body().toString()
    }

    fun sendMessage(chatId: Long, text: String): String? {
        val encoded = text.encodeUrl()
        val urlSendMess = "https://api.telegram.org/bot$botToken/sendMessage?chat_id=$chatId&text=$encoded"
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMess)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendQuestion(chatId: Long, question: Question?): String? {
        if (question == null) return "Все слова выучены"
        val correctAnswer = question.correctAnswer
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val answers = question.variants.filter { it != question.correctAnswer}.shuffled()
        val sendMenuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Выбрать правильный перевод: ${correctAnswer.original}",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "${correctAnswer.translate}",
            					"callback_data": "$CALLBACK_DATA_ANSWER_PREFIX "
            				},
            				{
            					"text": "${answers.random()}",
            					"callback_data": "$CALLBACK_DATA_ANSWER_PREFIX "
            				},
                            {
                            "text": "$question",
                            "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX"
                            },
                            {
                            "text": "$question",
                            "callback_data": "$CALLBACK_DATA_ANSWER_PREFIX"
                            }
            			]
            		]
            	}
            }
        """.trimIndent()
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()

        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }


    private fun String.encodeUrl(): String? = URLEncoder.encode(this, Charsets.UTF_8.name())

    fun sendMenu(botToken: String, chatId: Long): String? {
        val urlSendMessage = "https://api.telegram.org/bot$botToken/sendMessage"
        val sendMenuBody = """
            {
            	"chat_id": $chatId,
            	"text": "Основное меню",
            	"reply_markup": {
            		"inline_keyboard": [
            			[
            				{
            					"text": "Изучить слова",
            					"callback_data": "$LEARN_WORDS"
            				},
            				{
            					"text": "Статистика",
            					"callback_data": "$STATISTICS"
            				}
            			]
            		]
            	}
            }
        """.trimIndent()
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
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