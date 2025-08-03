package com.mobicom.s18.toledo.aaronace.sidequest.model

import com.google.firebase.firestore.DocumentId

data class UserModel(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val phoneNumber: String = "",
    val totalCompletedQuests: Int = 0,
    var rank: String = "Novice"
) {
    constructor() : this("", "", "", 0, "Novice")

    fun calculateRank() {
        when {
            totalCompletedQuests >= 50 -> rank = "Pilot"
            totalCompletedQuests >= 20 -> rank = "Voyager"
            totalCompletedQuests >= 10 -> rank = "Ranger"
            totalCompletedQuests >= 5 -> rank = "Roamer"
            else -> rank = "Newbie"
        }
    }
}