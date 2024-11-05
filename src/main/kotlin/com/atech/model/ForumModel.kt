package com.atech.model

import kotlinx.serialization.Serializable


@Serializable
data class ForumRequest(
    val forumModel: ForumModel,
    val message: MessageModel?
)


@Serializable
data class ForumModel(
    val createdChatUid: String,
    val createdChatUserName: String,
    val createdChatUserEmail: String,
    val createdChatProfileUrl: String,

    val receiverChatUid: String,
    val receiverChatUserName: String,
    val receiverChatUserEmail: String,
    val receiverChatProfileUrl: String,

    val path: String,
    val created: Long = System.currentTimeMillis()
)


fun ForumModel.getPath() =
    "${this.createdChatUid}_${this.createdChatUserName}_${this.receiverChatUid}_${this.receiverChatUserName}"

@Serializable
data class MessageModel(
    val senderName: String,
    val senderUid: String,
    val receiverName: String,
    val receiverUid: String,
    val message: String,
    val path: String,
    val created: Long = System.currentTimeMillis()
)
