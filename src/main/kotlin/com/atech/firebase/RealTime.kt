package com.atech.firebase

import com.atech.model.TagModel
import com.atech.utils.FirebaseRealtimeDatabasePath
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class GetAllTagUseCase(
    val db: FirebaseDatabase = FirebaseInstance.getFirebaseRealtime()
) {

    suspend operator fun invoke(): List<TagModel> = suspendCancellableCoroutine { continuation ->
        db.getReference(FirebaseRealtimeDatabasePath.TAGS.path)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tagList = mutableListOf<TagModel>()
                    for (childSnapshot in snapshot.children) {
                        val tagModel = childSnapshot.getValue(TagModel::class.java)
                        tagModel?.let { tagList.add(it) }
                    }
                    continuation.resume(tagList)
                }

                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
    }
}

data class AddTagUseCase(
    val db: FirebaseDatabase = FirebaseInstance.getFirebaseRealtime()
) {
    suspend operator fun invoke(tag: TagModel) = suspendCancellableCoroutine { continuation ->
        val ref = db.getReference(FirebaseRealtimeDatabasePath.TAGS.path)
//        check if tag already exists
        ref.child(tag.name.uppercase()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    continuation.resumeWithException(Exception("Tag already exists"))
                } else {
                    ref.child(tag.name.uppercase()).setValue(tag, { error, _ ->
                        if (error != null) {
                            continuation.resumeWithException(error.toException())
                        } else {
                            continuation.resume(Unit)
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }
}

data class DeleteTagUseCase(
    val db: FirebaseDatabase = FirebaseInstance.getFirebaseRealtime()
) {
    suspend operator fun invoke(tagName: String) = suspendCancellableCoroutine { continuation ->
        val ref = db.getReference(FirebaseRealtimeDatabasePath.TAGS.path)
        ref.child(tagName.uppercase()).removeValue { error, _ ->
            if (error != null) {
                continuation.resumeWithException(error.toException())
            } else {
                continuation.resume(Unit)
            }
        }
    }
}