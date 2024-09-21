package com.atech.firebase

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.database.FirebaseDatabase

object FirebaseInstance {
    fun getFirebaseFireStore(): Firestore = FirestoreClient.getFirestore()

    fun getFirebaseRealtime(): FirebaseDatabase = FirebaseDatabase.getInstance()
}