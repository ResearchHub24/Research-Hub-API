package com.atech.plugins

import com.atech.firebase.AddTagUseCase
import com.atech.firebase.DeleteTagUseCase
import com.atech.firebase.GetAllTagUseCase
import com.atech.model.TagModel
import com.atech.utils.RoutePaths
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.tagRouting() {
    routing {
        get(RoutePaths.TAGS.path) {
            try {
                val getAllTagUseCase = GetAllTagUseCase().invoke()
                call.respond(HttpStatusCode.OK, getAllTagUseCase)
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError, message = ErrorResponse("{error: ${e.message}}")
                )
            }
        }

        post(RoutePaths.TAGS.path) {
            try {
                val tag = call.receive<TagModel>()
                AddTagUseCase().invoke(tag)
                call.respond(HttpStatusCode.OK, "Tag added successfully")
            } catch (e: ContentTransformationException) {
                // Handle case where the request body couldn't be transformed into ResearchModel
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Invalid or missing ResearchModel in the request body"
                )
            } catch (e: Exception) {
                // Handle general errors
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Error: ${e.message}"
                )
            }
        }

        delete("${RoutePaths.TAGS.path}/delete") {
            val id = call.queryParameters["id"]
            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "{error: Missing id parameter}"
                )
                return@delete
            }
            try {
                DeleteTagUseCase().invoke(id)
                call.respond(HttpStatusCode.OK, "Tag deleted successfully")
            } catch (e: ContentTransformationException) {
                // Handle case where the request body couldn't be transformed into ResearchModel
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("{error: Invalid or missing ResearchModel in the request body}")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "{error: ${e.message}}"
                )
            }
        }
    }

}