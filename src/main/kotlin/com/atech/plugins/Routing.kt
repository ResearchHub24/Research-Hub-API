package com.atech.plugins

import com.atech.firebase.FirebaseInstance
import com.atech.firebase.GetUserDetailUserCase
import com.atech.firebase.UpdateUserDetailUserCase
import com.atech.model.UpdateQueryUser
import com.atech.utils.RatesPaths
import com.google.cloud.firestore.WriteResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
"""

@Serializable
data class ErrorResponse(val error: String)

@Serializable
data class SuccessResponse(val message: String)

fun Application.allRoutes() {
    configureRouting()
    postUserDetails()
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText(hello, contentType = ContentType.Text.Html)
        }
    }
}


fun Application.postUserDetails() {
    routing {
        get("${RatesPaths.POST_USER_DETAILS.path}/{userId}") {
            val getUser = GetUserDetailUserCase(
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
        updateUserDetails(this, application)
    }
}

private fun updateUserDetails(routing: Routing, application: Application) {
    routing.post("${RatesPaths.POST_USER_DETAILS.path}/{userId}/update") {
        val userID = call.parameters["userId"]
        if (userID.isNullOrEmpty() || userID.length < 5) {
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = ErrorResponse("error: User ID is missing")
            )
        }
        val getUser = GetUserDetailUserCase(
            fb = FirebaseInstance.getFirebaseFireStore()
        )
        val user = getUser(userID!!)
        if (user == null) {
            call.respond(
                status = HttpStatusCode.NotFound,
                message = ErrorResponse("error: User not found")
            )
        }
        val passWord = call.request.queryParameters[UpdateQueryUser.PasswordParam.query]
        val userType = call.request.queryParameters[UpdateQueryUser.UserTypeParam.query]
        val phone = call.request.queryParameters[UpdateQueryUser.PhoneParam.query]
        val educationDetails = call.request.queryParameters[UpdateQueryUser.EducationDetailsParam.query]
        val skillList = call.request.queryParameters[UpdateQueryUser.SkillListParam.query]
        val filledForm = call.request.queryParameters[UpdateQueryUser.FilledFormParam.query]
        val selectedForm = call.request.queryParameters[UpdateQueryUser.SelectedFormParam.query]
        val verified = call.request.queryParameters[UpdateQueryUser.VerifiedParam.query]
        val links = call.request.queryParameters[UpdateQueryUser.LinksParam.query]
        coroutineScope {
            val deferred: MutableList<Deferred<WriteResult>> = mutableListOf()
            try {
                if (userID.isEmpty() || userID.length < 5) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = ErrorResponse("{error: User ID is missing}")
                    )
                }
                if (passWord != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.PasswordParam, passWord)
                    })
                }
                if (userType != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.UserTypeParam, userType)
                    })
                }
                if (phone != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.PhoneParam, phone)
                    })
                }
                if (educationDetails != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.EducationDetailsParam, educationDetails)
                    })
                }
                if (skillList != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.SkillListParam, skillList)
                    })
                }
                if (filledForm != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.FilledFormParam, filledForm)
                    })
                }
                if (selectedForm != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.SelectedFormParam, selectedForm)
                    })
                }
                if (verified != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.VerifiedParam, verified.toBoolean())
                    })
                }
                if (links != null) {
                    deferred.plus(application.async {
                        val update = UpdateUserDetailUserCase(
                            fb = FirebaseInstance.getFirebaseFireStore()
                        )
                        update(userID, UpdateQueryUser.LinksParam, links)
                    })
                }
                deferred.awaitAll()
                call.respond(HttpStatusCode.OK, SuccessResponse("User details updated successfully for $userID"))
            } catch (e: Exception) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = ErrorResponse("error: ${e.message}")
                )
            }
        }
    }
}