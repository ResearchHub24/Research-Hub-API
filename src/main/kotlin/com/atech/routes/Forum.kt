package com.atech.routes

import com.atech.firebase.CreateForumUseCase
import com.atech.firebase.GetAllForumUseCase
import com.atech.firebase.GetAllMessagesUseCase
import com.atech.firebase.SendMessageUseCase
import com.atech.model.ForumRequest
import com.atech.model.MessageModel
import com.atech.utils.RoutePaths
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.forumRoutes() {
    routing {
        post("${RoutePaths.FORUM.path}/post") {
            try {
                val request = call.receive<ForumRequest>()
                val createForum = CreateForumUseCase()
                createForum.invoke(
                    request.forumModel,
                    request.message
                )
                call.respond(HttpStatusCode.OK, SuccessResponse("Message sent successfully"))
            } catch (e: ContentTransformationException) {
                // Handle case where the request body couldn't be transformed into ResearchModel
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("{error: Invalid or missing ForumRequest in the request body}")
                )
            } catch (e: Exception) {
                // Handle general errors
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("{error: ${e.message}}")
                )
            }
        }
        get("${RoutePaths.FORUM.path}/all") {
            try {
                val uid = call.parameters["uid"]
                if (uid == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("{error: uid is missing}")
                    )
                    return@get
                }
                val forAdmin: Boolean = call.parameters["isAdmin"].toBoolean()
                val getAll = GetAllForumUseCase()
                call.respond(
                    HttpStatusCode.OK,
                    getAll.invoke(uid, forAdmin)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("{error: ${e.message}}")
                )
            }
        }
        get("${RoutePaths.MESSAGES.path}/{path}") {
            try {
                val path = call.parameters["path"]
                if (path == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("{error: path is missing}")
                    )
                    return@get
                }
                val getAll = GetAllMessagesUseCase()
                call.respond(
                    HttpStatusCode.OK,
                    getAll.invoke(path)
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("{error: ${e.message}}")
                )
            }
        }
        post("${RoutePaths.MESSAGES.path}/post") {
            try {
                val path = call.parameters["path"]
                if (path == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ErrorResponse("{error: path is missing}")
                    )
                    return@post
                }
                val request = call.receive<MessageModel>()
                val sendMessage = SendMessageUseCase()
                sendMessage.invoke(path, request)
                call.respond(HttpStatusCode.OK, SuccessResponse("Message sent successfully"))
            } catch (e: ContentTransformationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse("{error: Invalid or missing MessageModel in the request body}")
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

