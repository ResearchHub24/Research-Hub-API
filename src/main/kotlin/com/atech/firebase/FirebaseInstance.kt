package com.atech.firebase

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient

object FirebaseInstance {
    fun getFirebaseFireStore(): Firestore = FirestoreClient.getFirestore()
}