package org.example

/*import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
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
    val text : String,
)
@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
)

fun main(){

    val json = Json{
        ignoreUnknownKeys = true
    }

    val responseToString = """
    {
        "ok": true,
        "result": [
            {
                "update_id": 748905861,
                "message": {
                    "message_id": 708,
                    "from": {
                        "id": 858786175,
                        "is_bot": false,
                        "first_name": "\u041a\u0441\u0435\u043d\u0438\u044f \u041f\u0435\u0440\u043c\u044f\u043a\u043e\u0432\u0430",
                        "username": "Ksu_Permyakova",
                        "language_code": "ru"
                    },
                    "chat": {
                        "id": 858786175,
                        "first_name": "\u041a\u0441\u0435\u043d\u0438\u044f \u041f\u0435\u0440\u043c\u044f\u043a\u043e\u0432\u0430",
                        "username": "Ksu_Permyakova",
                        "type": "private"
                    },
                    "date": 1758265678,
                    "text": "/start",
                    "entities": [
                        {
                            "offset": 0,
                            "length": 6,
                            "type": "bot_command"
                        }
                    ]
                }
            }
        ]
    }
    """.trimIndent()
    val response = json.decodeFromString<Response>(responseToString)
    println(response)

   /* val word = Json.encodeToString(
        Word(
            original = "Hello",
            translate = "Привет",
            correctAnswerCount = 0,
        )
    )

  println(word)

    val wordObject = Json.decodeFromString<Word>(
        """{"original": "Hello", "translate": "Привет"}"""
    )
  // println(wordObject)*/

}*/