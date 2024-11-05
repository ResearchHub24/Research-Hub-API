package com.atech.routes

import com.atech.firebase.*
import com.atech.model.*
import com.atech.utils.RoutePaths
import com.atech.utils.fromJson
import com.google.cloud.firestore.WriteResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.Serializable

private const val hello = """
    <html>
        <head>
            <title>Research Hub</title>
        </head>
        <body>
            <h1>
                <h1>Welcome to Research Hub Server</h1>
                Created by Ayaan, Shakya and Vidhi
            </h1>
        </body>
    </html>
"""

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class SuccessResponse(val message: String)

fun Application.allRoutes() {
    configureRouting()
    userDetails()
    logIn()
    researchRouting()
    tagRouting()
    researchApplication()
    sendNotification()
    sendMessage()
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(hello, contentType = ContentType.Text.Html)
        }
    }
}


fun Application.userDetails() {
    routing {
        get("${RoutePaths.POST_USER_DETAILS.path}/{userId}") {
            val getUser = GetUserDetailUseCase(
                fb = FirebaseInstance.getFirebaseFireStore()
            )
            val errorMessage = ErrorResponse("error: User ID is missing")
            val userId = call.parameters["userId"]
            if (userId.isNullOrEmpty() || userId.length < 5) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = errorMessage
                )
            } else {
                val user = getUser(userId)
                if (user == null) {
                    call.respond(
                        status = HttpStatusCode.NotFound,
                        message = ErrorResponse("error: User not found")
                    )
                } else {
                    call.respond(HttpStatusCode.OK, user)
                }
            }
        }
        get("${RoutePaths.POST_USER_DETAILS.path}/faculty") {
            try {
                val getAllFaculty = GetAllFacultyUseCase()
                val faculty = getAllFaculty()
                call.respond(HttpStatusCode.OK, faculty)
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.InternalServerError,
                    message = ErrorResponse("error: ${e.message}")
                )
            }
        }
        updateUserDetails(this, application)
    }
}

private fun updateUserDetails(routing: Routing, application: Application) {
    routing.post("${RoutePaths.POST_USER_DETAILS.path}/{userId}/update") {
        val userID = call.parameters["userId"]
        if (userID.isNullOrEmpty() || userID.length < 5) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse("error: User ID is missing or invalid")
            )
            return@post
        }

        val getUser = GetUserDetailUseCase(fb = FirebaseInstance.getFirebaseFireStore())
        val user = getUser(userID)
        if (user == null) {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = ErrorResponse("error: User not found")
            )
            return@post
        }

        val update = UpdateUserDetailUseCase(fb = FirebaseInstance.getFirebaseFireStore())
        val updateResults = mutableListOf<Result<WriteResult>?>()

        try {
            coroutineScope {
                listOf(
                    UpdateQueryUser.PasswordParam to call.request.queryParameters[UpdateQueryUser.PasswordParam.query],
                    UpdateQueryUser.UserTypeParam to call.request.queryParameters[UpdateQueryUser.UserTypeParam.query],
                    UpdateQueryUser.PhoneParam to call.request.queryParameters[UpdateQueryUser.PhoneParam.query],
                    UpdateQueryUser.VerifiedParam to call.request.queryParameters[UpdateQueryUser.VerifiedParam.query]?.toBoolean(),
                    UpdateQueryUser.EducationDetailsParam to call.request.queryParameters[UpdateQueryUser.EducationDetailsParam.query]?.fromJson<List<EducationDetails>>(),
                    UpdateQueryUser.SkillListParam to call.request.queryParameters[UpdateQueryUser.SkillListParam.query]?.fromJson<List<String>>(),
                    UpdateQueryUser.FilledFormParam to call.request.queryParameters[UpdateQueryUser.FilledFormParam.query]?.fromJson<List<FilledForm>>(),
                    UpdateQueryUser.SelectedFormParam to call.request.queryParameters[UpdateQueryUser.SelectedFormParam.query]?.fromJson<List<String>>(),
                    UpdateQueryUser.LinksParam to call.request.queryParameters[UpdateQueryUser.LinksParam.query]?.fromJson<List<LinkModel>>()
                ).forEach { (param, value) ->
                    if (value != null) {
                        val result = async { update(userID, param, value) }
                        updateResults.add(result.await())
                    }
                }
            }

            val errors = updateResults.mapNotNull { it?.exceptionOrNull()?.message }
            if (errors.isNotEmpty()) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("Errors occurred during update: ${errors.joinToString("; ")}")
                )
            } else {
                call.respond(HttpStatusCode.OK, SuccessResponse("User details updated successfully for $userID"))
            }
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = ErrorResponse("Error occurred during update: ${e.message}")
            )
        }
    }
}

fun Application.logIn() {
    routing {
        post(RoutePaths.LOGIN.path) {
            val email = call.request.queryParameters["email"]
            val password = call.request.queryParameters["password"]
            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("error: User ID is missing")
                )
                return@post
            }
            val getUser = LogInUseCase(
                db = FirebaseInstance.getFirebaseFireStore()
            )
            val user = getUser(email = email, password = password)
            if (user == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("error: User not found or password is incorrect")
                )
            } else {
                call.respond(
                    HttpStatusCode.OK, LoginResponse(
                        uid = user.uid ?: "",
                        email = user.email ?: "",
                        name = user.displayName ?: "",
                        photoUrl = user.photoUrl,
                        userType = user.userType ?: UserType.STUDENT.name
                    )
                )
            }
        }
    }
}