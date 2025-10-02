package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long?,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("callback_data")
    val callbackData: String,
    @SerialName("text")
    val text: String,
)

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