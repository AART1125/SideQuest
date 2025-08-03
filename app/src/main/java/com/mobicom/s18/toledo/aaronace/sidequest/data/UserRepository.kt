package com.mobicom.s18.toledo.aaronace.sidequest.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mobicom.s18.toledo.aaronace.sidequest.model.UserModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUserData(userId: String): Flow<UserModel?> {
        return callbackFlow {
            val listener = firestore.collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        val userData = snapshot.toObject(UserModel::class.java)
                        trySend(userData)
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }

    fun updateUserData(userId: String, userData: UserModel) {
        firestore.collection("users")
            .document(userId)
            .set(userData)
    }
}