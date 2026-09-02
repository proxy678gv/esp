package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    PLAYER,
    HOST,
    ADMIN,
    SUPER_ADMIN
}

enum class TournamentStatus {
    DRAFT,
    PENDING_APPROVAL,
    APPROVED,
    REGISTRATION_OPEN,
    REGISTRATION_CLOSED,
    CHECK_IN,
    LIVE,
    RESULTS_PENDING,
    UNDER_REVIEW,
    COMPLETED,
    CANCELLED
}

enum class GameType(val displayName: String, val publisher: String) {
    FREE_FIRE("Free Fire", "Garena"),
    BGMI("BGMI", "Krafton"),
    VALORANT_MOBILE("Valorant Mobile", "Riot Games"),
    CALL_OF_DUTY("Call of Duty: Mobile", "Activision")
}

enum class TournamentMode {
    BATTLE_ROYALE,
    CLASH_SQUAD
}

enum class TournamentFormat {
    SOLO,
    DUO,
    SQUAD,
    BEST_OF_1,
    BEST_OF_3,
    BEST_OF_5
}

enum class PGRankTier(val displayName: String, val minRating: Int, val badgeColor: Long) {
    BRONZE("Bronze", 0, 0xFFCD7F32),
    SILVER("Silver", 1000, 0xFFC0C0C0),
    GOLD("Gold", 1500, 0xFFFFD700),
    PLATINUM("Platinum", 2000, 0xFF00E5FF),
    DIAMOND("Diamond", 2600, 0xFF9966FF),
    MASTER("Master", 3200, 0xFFFF3366),
    GRANDMASTER("Grandmaster", 4000, 0xFFFF0055),
    LEGEND("Legend", 5000, 0xFF00FF88)
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val fullName: String,
    val email: String,
    val mobile: String,
    val role: UserRole = UserRole.PLAYER,
    val rankTier: PGRankTier = PGRankTier.GOLD,
    val rating: Int = 1680,
    val totalMatches: Int = 42,
    val totalWins: Int = 18,
    val totalKills: Int = 145,
    val winRatePercent: Float = 42.8f,
    val kdRatio: Float = 3.45f,
    val walletBalance: Double = 1250.0,
    val pendingBalance: Double = 400.0,
    val totalWinnings: Double = 8900.0,
    val avatarUrl: String = "",
    val inGameId: String = "PG_THUNDER_99",
    // Dedicated Free Fire Player Account Identity Details
    val freeFireUid: String = "1928374650",
    val freeFireIgn: String = "PG_THUNDER_99",
    val freeFireLevel: Int = 72,
    val freeFireRankTier: String = "Grandmaster",
    val freeFireServerRegion: String = "IND (India)",
    val freeFireBattleRole: String = "Rusher / Assaulter",
    val freeFireGuildName: String = "PG ESPORTS ELITE",
    val freeFireGuildId: String = "30098451",
    val freeFireLikes: Int = 14250,
    val freeFireHeadshotRate: Float = 48.5f,
    val freeFireCharacterMain: String = "Alok / Chrono",
    val isHostApproved: Boolean = false,
    val googleId: String = "1098472910398471029",
    val googleEmail: String = "proxyf388@gmail.com",
    val googleDisplayName: String = "Arun Karthik (Google User)",
    val googleAvatarUrl: String = "",
    val isGoogleLinked: Boolean = true,
    val googlePlayGamesId: String = "PG_GamerTag_9921",
    val lastCloudSyncTimestamp: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

data class GoogleAccountProfile(
    val googleId: String,
    val email: String,
    val displayName: String,
    val avatarUrl: String,
    val isVerified: Boolean = true,
    val playGamesTag: String = "PG_GamerTag_9921",
    val authProvider: String = "accounts.google.com",
    val tokenType: String = "Bearer (OAuth 2.0)",
    val scopeList: List<String> = listOf("email", "profile", "openid", "games"),
    val linkedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey val id: String,
    val hostId: String,
    val hostName: String,
    val gameId: String,
    val gameName: String,
    val title: String,
    val description: String,
    val bannerUrl: String,
    val mode: TournamentMode,
    val format: TournamentFormat,
    val mapName: String,
    val status: TournamentStatus,
    val entryFee: Double,
    val prizePool: Double,
    val prizeFirst: Double,
    val prizeSecond: Double,
    val prizeThird: Double,
    val maxTeams: Int,
    val registeredTeamsCount: Int,
    val serverRegion: String = "India (IN)",
    val startDateTimeMillis: Long,
    val endDateTimeMillis: Long,
    val checkInStartMillis: Long,
    val checkInEndMillis: Long,
    val registrationCloseMillis: Long,
    val isFeatured: Boolean = false,
    val rules: String = "1. Emulator/hacks strictly prohibited. 2. All team players must check in 15 mins prior. 3. Room ID will be auto-released on this app only.",
    val killPoints: Int = 2,
    val firstPlacementPoints: Int = 15,
    val rejectionReason: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "registrations")
data class RegistrationEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val teamId: String,
    val teamName: String,
    val captainId: String,
    val captainName: String,
    val playerUid: String = "1928374650",
    val playerIgn: String = "PG_THUNDER_99",
    val slotNumber: Int,
    val status: String = "CONFIRMED", // CONFIRMED, PAYMENT_PENDING, CANCELLED
    val paymentId: String,
    val isCheckedIn: Boolean = false,
    val checkInTime: Long? = null,
    val registeredAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: String,
    val name: String,
    val tag: String,
    val game: String,
    val captainId: String,
    val captainName: String,
    val memberNames: String, // Comma separated
    val substituteNames: String = "",
    val totalMatches: Int = 24,
    val totalWins: Int = 11,
    val logoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "match_rooms")
data class MatchRoomEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val matchNumber: Int = 1,
    val roundName: String = "Finals Round 1",
    val roomId: String = "8839201",
    val roomPassword: String = "PG9920",
    val isReleased: Boolean = false,
    val releaseTimeMillis: Long,
    val status: String = "SCHEDULED", // SCHEDULED, CHECK_IN, ROOM_READY, LIVE, COMPLETED
    val serverTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "results")
data class TournamentResultEntity(
    @PrimaryKey val id: String,
    val tournamentId: String,
    val matchId: String,
    val teamId: String,
    val teamName: String,
    val placement: Int,
    val kills: Int,
    val placementPoints: Int,
    val killPoints: Int,
    val bonus: Int = 0,
    val penalty: Int = 0,
    val totalScore: Int,
    val prizeAmount: Double = 0.0,
    val status: String = "VERIFIED", // SUBMITTED, UNDER_REVIEW, VERIFIED, REJECTED
    val proofImageUrl: String = "",
    val rejectionReason: String = "",
    val verifiedAt: Long? = null
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String, // ENTRY_FEE, PRIZE_CREDIT, DEPOSIT, WITHDRAWAL
    val amount: Double,
    val status: String, // SUCCESS, PENDING, COMPLETED, FAILED
    val description: String,
    val providerReference: String = "RZP_TXN_9921",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "host_applications")
data class HostApplicationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val fullName: String,
    val username: String,
    val email: String,
    val mobile: String,
    val city: String,
    val gamingExperience: String,
    val gamesInterested: String,
    val hostingExperience: String,
    val reason: String,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val reviewerNotes: String = "",
    val submittedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "dispute_reports")
data class DisputeReportEntity(
    @PrimaryKey val id: String,
    val reporterId: String,
    val reporterName: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val category: String, // CHEATING, WRONG_RESULT, ROOM_PROBLEM, PAYMENT_ISSUE, OTHER
    val description: String,
    val priority: String = "HIGH", // NORMAL, HIGH, URGENT
    val status: String = "OPEN", // OPEN, UNDER_REVIEW, RESOLVED, REJECTED
    val resolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val category: String, // TOURNAMENT, MATCH_ROOM, WALLET, ADMIN, SYSTEM
    val deepLink: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actorId: String,
    val actorRole: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "support_tickets")
data class SupportTicketEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val subject: String,
    val category: String,
    val message: String,
    val status: String = "OPEN", // OPEN, UNDER_REVIEW, RESOLVED, CLOSED
    val adminReply: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
