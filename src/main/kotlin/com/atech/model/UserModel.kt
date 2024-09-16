package com.atech.model

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

@Serializable
enum class UserType {
    STUDENT, TEACHER
}

@Serializable
data class UserModel(
    val uid: String? = null,
    val email: String? = null,
    val password: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val created: Long = System.currentTimeMillis(),
    val phone: String? = null,
    val userType: String? = UserType.STUDENT.name,
//    Student
    val educationDetails: String? = null,
    val skillList: String? = null,
    val filledForm: String? = null,
    val selectedForm: String? = null,
//    Teacher
    val verified: Boolean? = false,
    val links: String? = null,
)

sealed class UpdateQueryUser<out T : Any>(
    val query: String,
    val type: KClass<@UnsafeVariance T>
) {
    data object PasswordParam : UpdateQueryUser<String>("password", String::class)
    data object UserTypeParam : UpdateQueryUser<String>("userType", String::class)
    data object PhoneParam : UpdateQueryUser<String>("phone", String::class)
    data object EducationDetailsParam : UpdateQueryUser<String>("educationDetails", String::class)
    data object SkillListParam : UpdateQueryUser<String>("skillList", String::class)
    data object FilledFormParam : UpdateQueryUser<String>("filledForm", String::class)
    data object SelectedFormParam : UpdateQueryUser<String>("selectedForm", String::class)
    data object VerifiedParam : UpdateQueryUser<Boolean>("verified", Boolean::class)
    data object LinksParam : UpdateQueryUser<String>("links", String::class)
}