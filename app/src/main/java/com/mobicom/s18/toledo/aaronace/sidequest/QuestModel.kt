package com.mobicom.s18.toledo.aaronace.sidequest

data class QuestModel(
    val id: Int,
    val title: String,
    val details: String,
    val location: String,
    var isCompleted: Boolean
)