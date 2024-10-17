package com.atech.plugins

import com.atech.utils.RoutePaths
import com.atech.utils.Skills
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.researchApplication() {
    routing {
        get(RoutePaths.SKILLS.path) {
            call.respond(
                HttpStatusCode.OK,
                Skills.skillList
            )
        }
    }
}