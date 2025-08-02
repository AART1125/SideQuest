package com.mobicom.s18.toledo.aaronace.sidequest.tracking

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.mobicom.s18.toledo.aaronace.sidequest.R
import com.mobicom.s18.toledo.aaronace.sidequest.model.QuestModel
import com.mobicom.s18.toledo.aaronace.sidequest.ui.theme.main.MainActivity

class QuestNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID = "quest_location_channel"
        private const val NOTIFICATION_ID = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Quest Location Notifications"
            val descriptionText = "Notifications when you're near quest locations"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                // Enable vibration and sound
                enableVibration(true)
                setShowBadge(true)
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showQuestLocationNotification(nearbyQuests: List<QuestModel>) {
        if (!hasNotificationPermission()) return

        val questCount = nearbyQuests.size
        val title = "Quests available in current location"
        val content = when {
            questCount == 1 -> "You have 1 quest available nearby: ${nearbyQuests.first().title}"
            questCount > 1 -> "You have $questCount quests available nearby"
            else -> return
        }

        // Create intent to open the app and navigate to map
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_map", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.location) // Use your location icon
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(buildDetailedContent(nearbyQuests))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500)) // Custom vibration pattern

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(NOTIFICATION_ID, builder.build())
            }
        } catch (securityException: SecurityException) {
            // Handle notification permission error
        }
    }

    private fun buildDetailedContent(nearbyQuests: List<QuestModel>): String {
        return if (nearbyQuests.size == 1) {
            val quest = nearbyQuests.first()
            "${quest.title}\n📍 ${quest.location}\n${quest.details}"
        } else {
            val questTitles = nearbyQuests.take(3).joinToString("\n• ") { it.title }
            "Nearby quests:\n• $questTitles" +
                    if (nearbyQuests.size > 3) "\n...and ${nearbyQuests.size - 3} more" else ""
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Notification permission is granted by default on older versions
        }
    }

    fun cancelAllNotifications() {
        try {
            NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            // Handle error
        }
    }
}