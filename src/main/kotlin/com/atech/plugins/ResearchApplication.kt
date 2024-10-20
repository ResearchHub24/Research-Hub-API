package com.atech.plugins

import com.atech.firebase.PostApplication
import com.atech.model.ApplicationModel
import com.atech.utils.RoutePaths
import com.atech.utils.Skills
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.researchApplication() {
    routing {
        get(RoutePaths.SKILLS.path) {
            call.respond(
                HttpStatusCode.OK, Skills.skillList
            )
        }

        post("${RoutePaths.ALL_RESEARCH.path}/{researchId}/apply") {
            val researchId = call.parameters["researchId"]
            if (researchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("User ID is missing")
                )
                return@post
            }
            val model = call.receive<ApplicationModel>()
            val postApplication = PostApplication()

            try {
                postApplication.invoke(researchId, model)
                call.respond(
                    HttpStatusCode.OK, SuccessResponse("Application submitted successfully")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}")
                )
            }
        }
    }
}