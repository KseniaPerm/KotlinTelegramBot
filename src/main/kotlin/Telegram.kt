package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(val botToken: String) {
    val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(updateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=${this}"
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body().toString()
    }

    fun String.encodeUrl(): String? = URLEncoder.encode(this, Charsets.UTF_8.name())

    fun sendMessage(chatId: Long, text: String): String? {
        val encoded = text.encodeUrl()
        val urlSendMess = "https://api.telegram.org/bot${this}/sendMessage?chat_id=$chatId&text=$encoded"
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMess)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }
}

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService(botToken)

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue
        val updateIdString = updates.substring(startUpdateId + 11, endUpdateId)
        println(updateIdString)
        updateId = updateIdString.toInt() + 1

        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
        val matchResult1: MatchResult? = chatIdRegex.find(updates)
        val groups1: MatchGroupCollection? = matchResult1?.groups
        val chatId: Long = groups1?.get(1)?.value?.toLongOrNull() ?: continue

        telegramBotService.sendMessage(chatId, "$text")
    }
}