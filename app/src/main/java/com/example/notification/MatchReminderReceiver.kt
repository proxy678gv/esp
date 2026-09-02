package com.example.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MatchReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val tournamentId = intent.getStringExtra(MatchNotificationHelper.EXTRA_TOURNAMENT_ID) ?: "TOUR_DEFAULT"
        val tournamentTitle = intent.getStringExtra(MatchNotificationHelper.EXTRA_TOURNAMENT_TITLE) ?: "Free Fire Tournament"
        val roomId = intent.getStringExtra(MatchNotificationHelper.EXTRA_ROOM_ID) ?: "9823412"
        val roomPass = intent.getStringExtra(MatchNotificationHelper.EXTRA_ROOM_PASS) ?: "PG99"
        val slotNumber = intent.getIntExtra(MatchNotificationHelper.EXTRA_SLOT_NUM, 12)

        MatchNotificationHelper.show15MinReminderNotification(
            context = context,
            tournamentId = tournamentId,
            tournamentTitle = tournamentTitle,
            roomId = roomId,
            roomPassword = roomPass,
            slotNumber = slotNumber
        )
    }
}
