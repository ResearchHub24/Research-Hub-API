package com.atech.model

import kotlinx.serialization.Serializable


@Serializable
data class ApplicationModel(
    val userName: String = "",
    val userUid: String = "",
    val userProfile: String = "",
    val researchId: String = "",
    val userEmail: String = "",
    var researchTitle: String = "",
    val selected: Boolean = false,
    val answers: List<AnswerModel> = emptyList(),
    val created: Long = System.currentTimeMillis()
)

@Serializable
data class AnswerModel(
    val question: String = "",
    val answer: String = "",
)