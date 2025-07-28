package com.mobicom.s18.toledo.aaronace.sidequest.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class QuestModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val details: String = "",
    val location: String = "",
    var completed: Boolean = false,
    val userId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    constructor() : this("", "", "", "", false, "", System.currentTimeMillis(), null)
}

fun Long?.toDateString(): String {
    if (this == null) return ""
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}