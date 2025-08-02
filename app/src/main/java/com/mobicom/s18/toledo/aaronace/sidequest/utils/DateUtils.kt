package com.mobicom.s18.toledo.aaronace.sidequest.utils

import java.text.SimpleDateFormat
import java.util.*

fun Long?.toDateString(): String {
    if (this == null) return ""
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long?.toShortDateString(): String {
    if (this == null) return ""
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}