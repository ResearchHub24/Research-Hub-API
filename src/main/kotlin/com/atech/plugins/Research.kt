package com.atech.plugins

import com.atech.firebase.*
import com.atech.model.ResearchModel
import com.atech.utils.RoutePaths
import com.atech.utils.fromJson
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.researchRouting() {
    routing {
//        get("${RoutePaths.ALL_RESEARCH.path}/{researchId}") {
//            /**
//             * Get research by research ID
//             */
//            val researchId = call.parameters["researchId"]
//            if (researchId == null) {
//                call.respond(
//                    status = HttpStatusCode.BadRequest, message = ErrorResponse("{error: Research ID is missing}")
//                )
//                return@get
//            }
//            call.respond(HttpStatusCode.OK, "Research ID: $researchId")
//        }

        get(RoutePaths.ALL_RESEARCH.path) {
            /**
             * Get research by research ID
             */
            try {
                val userId = call.request.queryParameters["userId"]
                if (userId == null || userId.length < 5) {
                    call.respond(
                        HttpStatusCode.OK, GetAllResearchUseCase(
                            db = FirebaseInstance.getFirebaseFireStore()
                        ).invoke()
                    )
                    return@get
                }
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
                return@get
            }
        }

        post(RoutePaths.POST_RESEARCH.path) {
            try {
                val model = call.receive<ResearchModel>()
                call.respond(
                    HttpStatusCode.OK,
                    SuccessResponse(
                        PostResearchUseCase(
                            db = FirebaseInstance.getFirebaseFireStore()
                        ).invoke(model)
                    )
                )
            }  catch (e: ContentTransformationException) {
                // Handle case where the request body couldn't be transformed into ResearchModel
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("{error: Invalid or missing ResearchModel in the request body}")
                )
            } catch (e: Exception) {
                // Handle general errors
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("{error: ${e.message}}")
                )
            }
        }
        put(RoutePaths.POST_RESEARCH.path) {
            try {
                // Receive the ResearchModel from the request body
                val model = call.receive<ResearchModel>()

                // Perform the update operation
                val update = UpdateResearchUseCase(
                    db = FirebaseInstance.getFirebaseFireStore()
                ).invoke(model)

                // Send a success response with the updated data
                call.respond(HttpStatusCode.OK, SuccessResponse(update))
            } catch (e: ContentTransformationException) {
                // Handle case where the request body couldn't be transformed into ResearchModel
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("{error: Invalid or missing ResearchModel in the request body}")
                )
            } catch (e: Exception) {
                // Handle general errors
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("{error: ${e.message}}")
                )
            }
        }
    }
}
