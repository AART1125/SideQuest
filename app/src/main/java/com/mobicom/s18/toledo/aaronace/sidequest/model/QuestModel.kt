package com.mobicom.s18.toledo.aaronace.sidequest.model

import com.google.firebase.firestore.DocumentId

data class QuestModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val details: String = "",
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var completed: Boolean = false,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    constructor() : this("", "", "", "", 0.0, 0.0,false, "", System.currentTimeMillis(), null)
}

/*fun Long?.toDateString(): String {
    if (this == null) return ""
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}*/