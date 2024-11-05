package com.atech.routes

import com.atech.model.NotificationModel
import com.atech.model.Type
import com.atech.model.setTopic
import com.atech.model.toMessage
import com.atech.utils.RoutePaths
import com.atech.utils.Topics
import com.google.firebase.messaging.FirebaseMessaging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.sendNotification() {
    routing {
        post("${RoutePaths.Topics.path}/{topic}") {
            val topic = call.parameters["topic"]
            if (topic == null) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Topic is missing")
                )
                return@post
            }
            if (topic.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Topic is empty")
                )
                return@post
            }
            val topicEnum = try {
                Topics.valueOf(topic)
            } catch (e: IllegalArgumentException) {
                call.respond(
                    HttpStatusCode.BadRequest, ErrorResponse("Invalid topic")
                )
                return@post
            }
            val body = call.receiveNullable<NotificationModel>()?.setTopic(topicEnum.name) ?: kotlin.run {
                call.respond(status = HttpStatusCode.BadRequest, message = "Invalid body")
                return@post
            }
            FirebaseMessaging.getInstance().send(body.toMessage(Type.RESEARCH))
            call.respond(HttpStatusCode.OK, SuccessResponse("Notification sent successfully to ${body.message.topic}"))
        }
    }
}