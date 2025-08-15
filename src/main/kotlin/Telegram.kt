package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0

    while (true) {
        Thread.sleep(2000)
        val updates: String = getUpdates(botToken, updateId)
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

        val chatIdRegex: Regex = "\"chat\":\"[{]\"\"id\":\"(.+?)\"".toRegex()
        val matchResult1: MatchResult? = chatIdRegex.find(updates)
        val groups1: MatchGroupCollection? = matchResult1?.groups
        val chatId: Long = groups1?.get(1)?.value?.toLongOrNull() ?: continue

        sendMessage(botToken,chatId,"$text")
        println(chatId)


    }
}

    fun getUpdates(botToken: String, upDateId: Int): String {
        val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$upDateId"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body().toString()
    }

    fun sendMessage(botToken: String, chatId: Long, text: String) {
        val urlSendMessage = "http://api.telegram.org/bot$botToken/sendMessage"
        val client: HttpClient = HttpClient.newBuilder().build()
        val request: HttpRequest? = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        val response: HttpResponse<String?> = client.send(request, HttpResponse.BodyHandlers.ofString())
    }
