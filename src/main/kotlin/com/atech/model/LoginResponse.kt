package com.atech.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val uid: String,
    val email: String,
    val name: String,
    val photoUrl: String? = null,
    val userType: String,
)