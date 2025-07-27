package com.mobicom.s18.toledo.aaronace.sidequest.model

import com.google.firebase.firestore.DocumentId

data class UserModel(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val totalCompletedQuests: Int = 0,
    val rank: String = "Novice"
) {
    constructor() : this("", "", "", 0, "Novice")

    fun calculateRank(): String {
        return when {
            totalCompletedQuests >= 50 -> "Pilot"
            totalCompletedQuests >= 20 -> "Voyager"
            totalCompletedQuests >= 10 -> "Ranger"
            totalCompletedQuests >= 5 -> "Roamer"
            else -> "Newbie"
        }
    }
}