package com.atech.utils

const val BASE = "/api/v1"

enum class RoutePaths(val path: String) {
    POST_USER_DETAILS("$BASE/users"),
    LOGIN("$BASE/login"),
    ALL_RESEARCH("$BASE/research"),
    POST_RESEARCH("$BASE/research/post"),
    DELETE_RESEARCH("$BASE/research/delete"),
    TAGS("${ALL_RESEARCH.path}/tags"),
    SKILLS("${ALL_RESEARCH.path}/skills"),
    SEND_NOTIFICATION("$BASE/notification"),
    Topics(SEND_NOTIFICATION.path + "/topics")
}

