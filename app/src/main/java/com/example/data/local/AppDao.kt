package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserFlow(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUser(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE googleId = :googleId LIMIT 1")
    fun getUserByGoogleId(googleId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY rating DESC")
    fun getAllUsersByRank(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET googleId = :googleId, googleEmail = :googleEmail, googleDisplayName = :googleDisplayName, isGoogleLinked = :isLinked, lastCloudSyncTimestamp = :syncTime WHERE id = :userId")
    suspend fun updateGoogleAccount(userId: String, googleId: String, googleEmail: String, googleDisplayName: String, isLinked: Boolean, syncTime: Long)

    @Query("UPDATE users SET freeFireUid = :uid, freeFireIgn = :ign, freeFireLevel = :level, freeFireRankTier = :rankTier, freeFireServerRegion = :serverRegion, freeFireBattleRole = :battleRole, freeFireGuildName = :guildName, inGameId = :ign WHERE id = :userId")
    suspend fun updateFreeFireAccountDetails(
        userId: String,
        uid: String,
        ign: String,
        level: Int,
        rankTier: String,
        serverRegion: String,
        battleRole: String,
        guildName: String
    )

    @Query("UPDATE users SET lastCloudSyncTimestamp = :syncTime WHERE id = :userId")
    suspend fun updateCloudSyncTimestamp(userId: String, syncTime: Long)

    // Database Metrics
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int

    @Query("SELECT COUNT(*) FROM tournaments")
    suspend fun getTournamentCount(): Int

    @Query("SELECT COUNT(*) FROM teams")
    suspend fun getTeamCount(): Int

    @Query("SELECT COUNT(*) FROM registrations")
    suspend fun getRegistrationCount(): Int

    @Query("SELECT COUNT(*) FROM wallet_transactions")
    suspend fun getTransactionCount(): Int

    // --- Tournaments ---
    @Query("SELECT * FROM tournaments ORDER BY isFeatured DESC, startDateTimeMillis ASC")
    fun getAllTournaments(): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE id = :tournamentId LIMIT 1")
    fun getTournamentById(tournamentId: String): Flow<TournamentEntity?>

    @Query("SELECT * FROM tournaments WHERE hostId = :hostId ORDER BY createdAt DESC")
    fun getTournamentsByHost(hostId: String): Flow<List<TournamentEntity>>

    @Query("SELECT * FROM tournaments WHERE status = 'PENDING_APPROVAL' ORDER BY createdAt DESC")
    fun getPendingApprovalTournaments(): Flow<List<TournamentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: TournamentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournaments(tournaments: List<TournamentEntity>)

    @Update
    suspend fun updateTournament(tournament: TournamentEntity)

    @Query("DELETE FROM tournaments WHERE id = :tournamentId")
    suspend fun deleteTournament(tournamentId: String)

    // --- Registrations ---
    @Query("SELECT * FROM registrations ORDER BY registeredAt DESC")
    fun getAllRegistrations(): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId ORDER BY slotNumber ASC")
    fun getRegistrationsForTournament(tournamentId: String): Flow<List<RegistrationEntity>>

    @Query("SELECT * FROM registrations WHERE captainId = :userId ORDER BY registeredAt DESC")
    fun getUserRegistrations(userId: String): Flow<List<RegistrationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: RegistrationEntity)

    @Update
    suspend fun updateRegistration(registration: RegistrationEntity)

    // --- Teams ---
    @Query("SELECT * FROM teams ORDER BY createdAt DESC")
    fun getAllTeams(): Flow<List<TeamEntity>>

    @Query("SELECT * FROM teams WHERE captainId = :userId")
    fun getUserTeams(userId: String): Flow<List<TeamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeam(team: TeamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeams(teams: List<TeamEntity>)

    @Query("DELETE FROM teams WHERE id = :teamId")
    suspend fun deleteTeam(teamId: String)

    // --- Match Rooms ---
    @Query("SELECT * FROM match_rooms WHERE tournamentId = :tournamentId LIMIT 1")
    fun getMatchRoom(tournamentId: String): Flow<MatchRoomEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchRoom(matchRoom: MatchRoomEntity)

    @Update
    suspend fun updateMatchRoom(matchRoom: MatchRoomEntity)

    // --- Results ---
    @Query("SELECT * FROM results WHERE tournamentId = :tournamentId ORDER BY totalScore DESC")
    fun getResultsForTournament(tournamentId: String): Flow<List<TournamentResultEntity>>

    @Query("SELECT * FROM results WHERE status = 'SUBMITTED' OR status = 'UNDER_REVIEW'")
    fun getPendingResults(): Flow<List<TournamentResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: TournamentResultEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResults(results: List<TournamentResultEntity>)

    @Update
    suspend fun updateResult(result: TournamentResultEntity)

    // --- Wallet Transactions ---
    @Query("SELECT * FROM wallet_transactions WHERE userId = :userId ORDER BY timestamp DESC")
    fun getWalletTransactions(userId: String): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<WalletTransactionEntity>)

    // --- Host Applications ---
    @Query("SELECT * FROM host_applications ORDER BY submittedAt DESC")
    fun getAllHostApplications(): Flow<List<HostApplicationEntity>>

    @Query("SELECT * FROM host_applications WHERE userId = :userId LIMIT 1")
    fun getUserHostApplication(userId: String): Flow<HostApplicationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHostApplication(app: HostApplicationEntity)

    @Update
    suspend fun updateHostApplication(app: HostApplicationEntity)

    // --- Dispute Reports ---
    @Query("SELECT * FROM dispute_reports ORDER BY createdAt DESC")
    fun getAllDisputeReports(): Flow<List<DisputeReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDisputeReport(report: DisputeReportEntity)

    @Update
    suspend fun updateDisputeReport(report: DisputeReportEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserNotifications(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsRead(userId: String)

    // --- Audit Logs ---
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)

    // --- Support Tickets ---
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllSupportTickets(): Flow<List<SupportTicketEntity>>

    @Query("SELECT * FROM support_tickets WHERE userId = :userId ORDER BY createdAt DESC")
    fun getUserSupportTickets(userId: String): Flow<List<SupportTicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportTicket(ticket: SupportTicketEntity)

    @Update
    suspend fun updateSupportTicket(ticket: SupportTicketEntity)
}
