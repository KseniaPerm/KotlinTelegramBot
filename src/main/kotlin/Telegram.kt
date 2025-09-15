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

        val newUpdateId = updateIdRegex.find(updates)?.groups?.get(1)?.value?.toIntOrNull() ?: continue
        updateId = newUpdateId + 1

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value?.decodeUnicode()
        println(text)

        val chatId: Long = chatIdRegex.find(updates)?.groups?.get(1)?.value?.toLongOrNull() ?: continue
        val data = dataRegex.find(updates)?.groups?.get(1)?.value?.decodeUnicode()

        when {
            text?.lowercase() == MENU || text == START -> {
                telegramBotService.sendMenu(botToken, chatId)
            }

            data?.lowercase() == STATISTICS -> {
                val trainerStat = trainer.getStatistics()
                telegramBotService.sendMessage(
                    chatId, "Всего слов: ${trainerStat.totalCount}, Выучено: ${trainerStat.learnedCount}," +
                            " Статистика: ${trainerStat.percent}"
                )
            }

            data?.lowercase() == LEARN_WORDS -> {
                telegramBotService.checkNextQuestionAndSend(trainer, telegramBotService, chatId)
            }

            data?.lowercase() == MENU -> {
                telegramBotService.sendMenu(botToken, chatId)
            }

            data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) ?: false -> {

                val userAnswerIndex = data.removePrefix(CALLBACK_DATA_ANSWER_PREFIX).toIntOrNull()

                val isCorrect = trainer.checkAnswer(userAnswerIndex)
                if (isCorrect) {
                    telegramBotService.sendMessage(chatId, "Правильно")
                } else {
                    telegramBotService.sendMessage(chatId, "Неправильно")
                }

                telegramBotService.checkNextQuestionAndSend(trainer, telegramBotService, chatId)

            }
        }
    }
}

fun String.decodeUnicode(): String {
    return this.replace("""\\u([0-9a-fA-F]{4})""".toRegex()) { matchResult ->
        val hexCode = matchResult.groupValues[1]
        val codePoint = hexCode.toInt(16)
        String(Character.toChars(codePoint))
    }
}