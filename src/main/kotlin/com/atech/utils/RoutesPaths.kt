package com.atech.utils
const val BASE = "/api/v1"

enum class RoutePaths(val path: String) {
    POST_USER_DETAILS("$BASE/users"),
    LOGIN("$BASE/login"),
}

