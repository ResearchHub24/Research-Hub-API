package com.atech.firebase

import com.atech.model.*
import com.atech.utils.FirebaseCollectionPath
import com.atech.utils.FirebaseDocumentPath
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.WriteResult
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.reflect.KClass

data class GetUserDetailUseCase(
    private val fb: Firestore
) {
    suspend operator fun invoke(uid: String) = withContext(Dispatchers.IO) {
        fb.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.USERS.path).document(uid).get().get()
    }.toObject(UserModel::class.java)
}


data class UpdateUserDetailUseCase(
    val fb: Firestore
) {
    suspend inline operator fun <reified T : Any> invoke(
        uid: String, parameter: UpdateQueryUser<Any>, value: T
    ): Result<WriteResult>? = withContext(Dispatchers.IO) {
        try {
            if (!isTypeMatch(parameter.type, value)) {
                return@withContext Result.failure(
                    IllegalArgumentException("Type mismatch for ${parameter.query}: expected ${parameter.type}, got ${value::class}")
                )
            }

            val result = fb.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
                .collection(FirebaseCollectionPath.USERS.path).document(uid).update(parameter.query, value).get()

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isTypeMatch(parameterType: KClass<*>, value: Any): Boolean {
        return when {
            parameterType == List::class && value is List<*> -> true
            parameterType == value::class -> true
            else -> false
        }
    }
}

data class LogInUseCase(
    val db: Firestore
) {
    suspend operator fun invoke(email: String, password: String): UserModel? = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.USERS.path).whereEqualTo("email", email)
            .whereEqualTo("password", password).get().get().toObjects(UserModel::class.java).getOrNull(0)
    }
}

data class GetAllResearchUseCase(
    val db: Firestore
) {
    suspend operator fun invoke(): List<ResearchModel> = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).get().get().toObjects(ResearchModel::class.java)
            .sortedByDescending { it.created }
    }
}


data class GetResearchByIdUseCase(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend fun invoke(researchId: String): ResearchModel? = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(researchId).get().get()
            .toObject(ResearchModel::class.java)
    }
}

data class PostResearchUseCase(
    val db: Firestore
) {
    suspend operator fun invoke(research: ResearchModel): String = withContext(Dispatchers.IO) {
        val ref = db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document()
        ref.set(research.copy(path = ref.id)).get()
        ref.id
    }
}

data class GetPostedResearchUseCase(
    val db: Firestore
) {
    suspend operator fun invoke(
        authorUid: String
    ): List<ResearchModel> = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).whereEqualTo("authorUid", authorUid).get().get()
            .toObjects(ResearchModel::class.java)
    }
}


data class UpdateResearchUseCase(
    val db: Firestore
) {
    suspend operator fun invoke(
        research: ResearchModel
    ): String = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(research.path).set(research).get()
        research.path
    }
}

data class DeleteResearchUseCase(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend operator fun invoke(
        researchId: String
    ): WriteResult? = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(researchId).delete().get()
    }
}

data class PostApplication(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend operator fun invoke(
        researchId: String, applicationModel: ApplicationModel
    ): WriteResult? = withContext(Dispatchers.IO) {

        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.USERS.path).document(applicationModel.userUid)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).document(researchId).set(applicationModel).get()

        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(researchId)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).document(applicationModel.userUid)
            .set(applicationModel).get()
    }
}

data class ChangeApplicationStatus(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend fun invoke(
        researchId: String, userUid: String, action: Action
    ): WriteResult? = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(researchId)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).document(userUid)
            .update("action", action)
            .get()

        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.USERS.path).document(userUid)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).document(researchId)
            .update("action", action)
            .get()
    }
}

data class GetAllFilledApplicationsUseCase(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend operator fun invoke(
        researchId: String
    ): List<ApplicationModel> = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(researchId)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).get().get().toObjects(ApplicationModel::class.java)
    }
}

data class GetAllFilledApplicationsStudentUseCase(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend operator fun invoke(
        userUid: String
    ): List<ApplicationModel> = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.USERS.path).document(userUid)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).get().get().toObjects(ApplicationModel::class.java)
    }
}

data class IsAppliedInResearchUseCase(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend operator fun invoke(
        researchId: String, userUid: String
    ): Boolean = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.RESEARCH.path).document(researchId)
            .collection(FirebaseCollectionPath.APPLICATIONS.path).document(userUid).get().get().exists()
    }
}

data class GetAllFacultyUseCase(
    val db: Firestore = FirebaseInstance.getFirebaseFireStore()
) {
    suspend operator fun invoke(): List<UserModel> = withContext(Dispatchers.IO) {
        db.collection(FirebaseCollectionPath.BASE.path).document(FirebaseDocumentPath.V1.path)
            .collection(FirebaseCollectionPath.USERS.path).whereEqualTo("userType", "TEACHER").get().get()
            .toObjects(UserModel::class.java)
    }
}


fun main() = runBlocking {
    val serviceAccountStream = this::class.java.classLoader.getResourceAsStream("serviceAccountKey.json")
    val option = FirebaseOptions.builder().setDatabaseUrl("https://researchhub-21392-default-rtdb.firebaseio.com")
        .setCredentials(GoogleCredentials.fromStream(serviceAccountStream)).build()
    FirebaseApp.initializeApp(option)
    val fb = FirebaseInstance.getFirebaseFireStore()
    val getUserDetailUserCase = GetUserDetailUseCase(fb)
    val user = getUserDetailUserCase.invoke("TF6YASVgyRQmytXTQRVm2c5NS2I2")
    println(user)
}