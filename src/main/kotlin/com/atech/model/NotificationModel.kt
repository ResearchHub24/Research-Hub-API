package com.atech.model

import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import kotlinx.serialization.Serializable


enum class Type {
    RESEARCH, FACULTY, USER_STUDENT, ADVERTISEMENT
}

@Serializable
data class NotificationModel(
    val message: MessageSend
)

@Serializable
data class MessageSend(
    val topic: String? = null,
    val to: String? = null,
    val notification: NotificationSend,
    val data: Data
)

@Serializable
data class NotificationSend(
    val title: String,
    val body: String,
)

@Serializable
data class Data(
    val key: String,
    val created: String,
    val image: String? = null
)


fun NotificationModel.setTopic(topic: String): NotificationModel =
    this.copy(message = message.copy(topic = topic))

fun NotificationModel.toMessage(type: Type): Message =
    Message.builder()
        .setNotification(
            Notification.builder()
                .setTitle(message.notification.title)
                .setBody(message.notification.body)
                .build()
        ).apply {
            if (message.to != null)
                setToken(message.to)
            else
                setTopic(message.topic)
        }
        .apply {
            with(message.data) {
                putData("key", key)
                putData("created", created)
                putData("type", type.toString())
                image?.let { putData("image", it) }
            }
        }
        .build()