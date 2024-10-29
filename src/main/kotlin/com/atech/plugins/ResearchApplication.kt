package com.atech.plugins

import com.atech.firebase.GetAllFilledApplicationsStudentUseCase
import com.atech.firebase.GetAllFilledApplicationsUseCase
import com.atech.firebase.IsAppliedInResearchUseCase
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
        get("${RoutePaths.ALL_RESEARCH.path}/{researchId}/applied") {
            val researchId = call.parameters["researchId"]
            if (researchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Research ID is missing")
                )
                return@get
            }
            val userId = call.queryParameters["userId"]
            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("User ID is missing")
                )
                return@get
            }
            val userCase = IsAppliedInResearchUseCase()
            try {
                val isApplied = userCase.invoke(researchId, userId)
                call.respond(
                    HttpStatusCode.OK, isApplied
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}")
                )
            }
        }
        get("${RoutePaths.POST_USER_DETAILS.path}/{userId}/applications") {
            val userId = call.parameters["userId"]
            if (userId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("User ID is missing")
                )
                return@get
            }
            val getUserApplications = GetAllFilledApplicationsStudentUseCase()

            try {
                val applications = getUserApplications.invoke(userId)
                call.respond(
                    HttpStatusCode.OK, applications
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}")
                )
            }
        }

        get("${RoutePaths.ALL_RESEARCH.path}/{researchId}/applications") {
            val researchId = call.parameters["researchId"]
            if (researchId == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Research ID is missing")
                )
                return@get
            }
            val userCase = GetAllFilledApplicationsUseCase()
            try {
                val isApplied = userCase.invoke(researchId)
                call.respond(
                    HttpStatusCode.OK, isApplied
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError, ErrorResponse("Error: ${e.message}")
                )
            }
        }
    }
}