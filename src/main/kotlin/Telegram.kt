package org.example

fun main(args: Array<String>) {

    val botToken = args[0]
    var updateId = 0
    val telegramBotService = TelegramBotService(botToken)
    val updateIdRegex: Regex = "\"update_id\":([0-9]+)[,}]".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()
    val trainer = LearnWordsTrainer()

    while (true) {
        Thread.sleep(2000)
        val updates: String = telegramBotService.getUpdates(updateId)
        println(updates)

        val updateIdGroups = updateIdRegex.find(updates)?.groups
        val newUpdateId = updateIdGroups?.get(1)?.value?.toInt() ?: 0
        updateId = newUpdateId + 1


        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value?.decodeUnicode()
        println(text)

        val matchResult1: MatchResult? = chatIdRegex.find(updates)
        val groups1: MatchGroupCollection? = matchResult1?.groups
        val chatId: Long = groups1?.get(1)?.value?.toLongOrNull() ?: continue

        val data = dataRegex.find(updates)?.groups?.get(1)?.value?.decodeUnicode()

        if (text?.lowercase() == "hello") {
            telegramBotService.sendMessage(chatId, "Hello")
        }
        if (text?.lowercase() == MENU && text == START) {
            telegramBotService.sendMenu(botToken, chatId)
        }
        if (data?.lowercase() == STATISTICS) {
            val trainerStat = trainer.getStatistics()
            telegramBotService.sendMessage(
                chatId, "Всего слов: ${trainerStat.totalCount}, Выучено: ${trainerStat.learnedCount}," +
                        " Статистика: ${trainerStat.percent}"
            )
        }
        if (data?.lowercase() == LEARN_WORDS) {
            telegramBotService.sendMessage(chatId, "Изучение слов")
        }

        telegramBotService.sendMessage(chatId, "$text")
        telegramBotService.sendMenu(botToken, chatId)
    }
}

fun String.decodeUnicode(): String {
    return this.replace("""\\u([0-9a-fA-F]{4})""".toRegex()) { matchResult ->
        val hexCode = matchResult.groupValues[1]
        val codePoint = hexCode.toInt(16)
        String(Character.toChars(codePoint))
    }
}