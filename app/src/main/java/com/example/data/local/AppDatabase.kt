package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.*

@Database(
    entities = [
        UserEntity::class,
        TournamentEntity::class,
        RegistrationEntity::class,
        TeamEntity::class,
        MatchRoomEntity::class,
        TournamentResultEntity::class,
        WalletTransactionEntity::class,
        HostApplicationEntity::class,
        DisputeReportEntity::class,
        NotificationEntity::class,
        AuditLogEntity::class,
        SupportTicketEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pg_esports_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
