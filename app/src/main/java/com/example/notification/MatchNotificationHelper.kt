package com.example.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object MatchNotificationHelper {
    const val CHANNEL_ID = "pg_esports_matches"
    const val CHANNEL_NAME = "PG Esports Match Reminders (15m Alert)"
    const val CHANNEL_DESC = "Urgent 15-minute countdown reminders before registered Free Fire matches start"

    const val EXTRA_TOURNAMENT_ID = "extra_tournament_id"
    const val EXTRA_TOURNAMENT_TITLE = "extra_tournament_title"
    const val EXTRA_ROOM_ID = "extra_room_id"
    const val EXTRA_ROOM_PASS = "extra_room_pass"
    const val EXTRA_SLOT_NUM = "extra_slot_num"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 100, 250, 100, 400)
                setShowBadge(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Schedules a local notification reminder 15 minutes before the match start time.
     */
    fun schedule15MinReminder(
        context: Context,
        tournamentId: String,
        tournamentTitle: String,
        matchStartTimeMillis: Long,
        roomId: String = "9823412",
        roomPassword: String = "PG99",
        slotNumber: Int = 12
    ) {
        createNotificationChannel(context)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val reminderTime = matchStartTimeMillis - (15 * 60 * 1000L)
        val currentTime = System.currentTimeMillis()
        val triggerTime = if (reminderTime > currentTime) reminderTime else currentTime + 1000L

        val intent = Intent(context, MatchReminderReceiver::class.java).apply {
            putExtra(EXTRA_TOURNAMENT_ID, tournamentId)
            putExtra(EXTRA_TOURNAMENT_TITLE, tournamentTitle)
            putExtra(EXTRA_ROOM_ID, roomId)
            putExtra(EXTRA_ROOM_PASS, roomPassword)
            putExtra(EXTRA_SLOT_NUM, slotNumber)
        }

        val requestCode = tournamentId.hashCode()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
        }
    }

    /**
     * Immediately triggers the 15-minute match reminder notification (used for testing and direct alerts)
     */
    fun show15MinReminderNotification(
        context: Context,
        tournamentId: String,
        tournamentTitle: String,
        roomId: String = "9823412",
        roomPassword: String = "PG99",
        slotNumber: Int = 12
    ) {
        createNotificationChannel(context)

        // Launch Intent directly to Match Room in MainActivity
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TOURNAMENT_ID, tournamentId)
            putExtra("ACTION_NAVIGATE_MATCH_ROOM", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            tournamentId.hashCode(),
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("🚨 MATCH STARTS IN 15 MINUTES!")
            .setContentText("$tournamentTitle • Slot #$slotNumber")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "🔥 $tournamentTitle\n" +
                        "⏰ Match begins in 15 minutes! Please check-in now.\n" +
                        "🔑 Custom Room ID: $roomId | Pass: $roomPassword\n" +
                        "🛡️ Slot #$slotNumber reserved. Good luck, Booyah await!"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setColor(0xFF00FF66.toInt()) // Neon Green
            .setSound(soundUri)
            .setVibrate(longArrayOf(0, 250, 100, 250, 100, 400))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_media_play,
                "Enter Match Room",
                pendingIntent
            )
            .build()

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(tournamentId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Notifications permission not granted by user yet
        }
    }

    fun cancelReminder(context: Context, tournamentId: String) {
        val intent = Intent(context, MatchReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            tournamentId.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )
        if (pendingIntent != null) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
