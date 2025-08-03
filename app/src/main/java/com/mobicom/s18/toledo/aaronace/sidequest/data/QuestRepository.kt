package com.mobicom.s18.toledo.aaronace.sidequest.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class QuestRepository {
    private val firestore = FirebaseFirestore.getInstance()

    fun getUserQuests(userId: String): Flow<List<QuestModel>> {
        return callbackFlow {
            val listener = firestore.collection("quests")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        close(e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null) {
                        val quests = snapshot.toObjects(QuestModel::class.java)
                        trySend(quests)
                    }
                }
            awaitClose {
                listener.remove()
            }
        }
    }

    suspend fun createQuest(quest: QuestModel, userId: String): Result<String> {
        return try {
            val questRef = firestore.collection("quests")
                .add(quest.copy(userId = userId))
                .await()
            Result.success(questRef.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun completeQuest(questId: String, userId: String): Result<Unit> {
        return try {
            val questDoc = firestore.collection("quests")
                .document(questId)
                .get()
                .await()

            if(questDoc.exists() && questDoc.getString("userId") == userId) {
                firestore.collection("quests")
                    .document(questId)
                    .update(
                        "completed", true,
                        "completedAt", System.currentTimeMillis()
                    )
                    .await()


                Result.success(Unit)
            } else {
                Result.failure(Exception("Quest not found or user mismatch"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}