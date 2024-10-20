package com.atech.utils

enum class FirebaseCollectionPath(val path: String) {
    BASE("ResearchHub"),
    USERS("Users"),
    RESEARCH("Research"),
    APPLICATIONS("Applications"),
}

enum class FirebaseDocumentPath(val path: String) {
    V1("v1"),
}

enum class FirebaseRealtimeDatabasePath(
    val path: String
) {
    TAGS("tags")
}