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
    val educationDetails: List<EducationDetails>? = null,
    val skillList: List<String>? = null,
    val filledForm: List<FilledForm>? = null,
    val selectedForm: List<String>? = null,
//    Teacher
    val verified: Boolean = false,
    val links: List<LinkModel>? = null,
)

@Serializable
data class EducationDetails(
    val university: String = "",
    val degree: String = "",
    val fieldOfStudy: String = "",
    val startYear: String = "",
    val endYear: String? = "",
    val grade: String? = "",
    val description: String = "",
    val created: Long? = null
)


@Serializable
data class LinkModel(
    val link: String = "",
    val description: String = "",
    val created: Long = System.currentTimeMillis(),
)

@Serializable
data class FilledForm(
    val formId: String = "",
    val answers: List<Answer> = emptyList(),
)

@Serializable
data class Answer(
    val question: String = "",
    val answer: String = "",
)

@SuppressWarnings("UNCHECKED_CAST")
@Suppress("UNCHECKED_CAST")
sealed class UpdateQueryUser<out T : Any>(
    val query: String,
    val type: KClass<@UnsafeVariance T>
) {
    data object PasswordParam : UpdateQueryUser<String>("password", String::class)
    data object UserTypeParam : UpdateQueryUser<String>("userType", String::class)
    data object PhoneParam : UpdateQueryUser<String>("phone", String::class)
    data object EducationDetailsParam :
        UpdateQueryUser<List<EducationDetails>>("educationDetails", List::class as KClass<List<EducationDetails>>)

    data object SkillListParam : UpdateQueryUser<List<String>>("skillList", List::class as KClass<List<String>>)
    data object FilledFormParam :
        UpdateQueryUser<List<FilledForm>>("filledForm", List::class as KClass<List<FilledForm>>)

    data object SelectedFormParam : UpdateQueryUser<List<String>>("selectedForm", List::class as KClass<List<String>>)
    data object VerifiedParam : UpdateQueryUser<Boolean>("verified", Boolean::class)
    data object LinksParam : UpdateQueryUser<List<LinkModel>>("links", List::class as KClass<List<LinkModel>>)
}