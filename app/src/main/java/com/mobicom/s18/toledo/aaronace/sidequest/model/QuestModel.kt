package com.mobicom.s18.toledo.aaronace.sidequest.model

import com.google.firebase.firestore.DocumentId

data class QuestModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val details: String = "",
    val location: String = "",
    var isCompleted: Boolean = false,
    val userId: String = "",
    val completedAt: Long? = null
) {
    constructor() : this("", "", "", "", false, "", null)

    constructor(
        id: Int,
        title: String,
        details: String,
        location: String,
        isCompleted: Boolean
    ) : this(
        id = id.toString(),
        title = title,
        details = details,
        location = location,
        isCompleted = isCompleted,
        userId = "",
        completedAt = null
    )
}