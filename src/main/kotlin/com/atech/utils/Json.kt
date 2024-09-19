package com.atech.utils

import kotlinx.serialization.json.Json

inline fun <reified T> String.fromJson(): T {
    return Json.decodeFromString(this)
}
