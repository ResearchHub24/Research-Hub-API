package com.atech.plugins

import com.atech.firebase.FirebaseInstance
import com.atech.firebase.GetAllResearchUseCase
import com.atech.firebase.GetPostedResearchUseCase
import com.atech.firebase.PostResearchUseCase
import com.atech.model.ResearchModel
import com.atech.utils.RoutePaths
import com.atech.utils.fromJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.researchRouting() {
    routing {
        get("${RoutePaths.ALL_RESEARCH.path}/{researchId}") {
            /**
             * Get research by research ID
             */
            val researchId = call.parameters["researchId"]
            if (researchId == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest, message = ErrorResponse("{error: Research ID is missing}")
                )
                return@get
            }
            call.respond(HttpStatusCode.OK, "Research ID: $researchId")
        }

        get(RoutePaths.ALL_RESEARCH.path) {
            /**
             * Get research by research ID
             */
            try {
                call.respond(
                    HttpStatusCode.OK, GetAllResearchUseCase(
                        db = FirebaseInstance.getFirebaseFireStore()
                    ).invoke()
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError, message = ErrorResponse("{error: ${e.message}}")
                )
                return@get
            }
        }

        get(RoutePaths.POST_RESEARCH.path) {
            /**
             * Get only posted researches by a user
             */
            val userId = call.request.queryParameters["userId"]
            if (userId == null || userId.length < 5) {
                call.respond(
                    status = HttpStatusCode.BadRequest, message = ErrorResponse("{error: User ID is missing}")
                )
                return@get
            }
            try {
                call.respond(
                    HttpStatusCode.OK,
                    GetPostedResearchUseCase(
                        db = FirebaseInstance.getFirebaseFireStore()
                    ).invoke(userId)
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError, message = ErrorResponse("{error: ${e.message}}")
                )
            }

        }
        post(RoutePaths.POST_RESEARCH.path) {
            val model = call.request.queryParameters["model"]?.fromJson<ResearchModel>()
            if (model == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest, message = ErrorResponse("{error: Model is missing}")
                )
                return@post
            }
            try {
                call.respond(
                    HttpStatusCode.OK,
                   SuccessResponse(
                       PostResearchUseCase(
                           db = FirebaseInstance.getFirebaseFireStore()
                       ).invoke(model)
                   )
                )
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError, message = ErrorResponse("{error: ${e.message}}")
                )
            }
        }
    }
}
