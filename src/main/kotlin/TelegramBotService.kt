package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(
    val botToken: String
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
    private fun String.encodeUrl(): String? = URLEncoder.encode(this, Charsets.UTF_8.name())
}