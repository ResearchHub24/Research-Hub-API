package com.atech.model

import kotlinx.serialization.Serializable

@Serializable
data class TagModel(
    val created: Long = System.currentTimeMillis(),
    val createdBy: String = "",
    val name: String = "",
)