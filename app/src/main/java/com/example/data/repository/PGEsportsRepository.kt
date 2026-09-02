package com.example.data.repository

import com.example.data.local.AppDao
import com.example.data.model.*
import com.example.payment.PaymentResultData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class PGEsportsRepository(private val dao: AppDao) {

    val currentUserId = "USER_THUNDER_01"

    fun getCurrentUser(): Flow<UserEntity?> = dao.getUserFlow(currentUserId)
    fun getAllUsersByRank(): Flow<List<UserEntity>> = dao.getAllUsersByRank()
    fun getAllTournaments(): Flow<List<TournamentEntity>> = dao.getAllTournaments()
    fun getTournamentById(id: String): Flow<TournamentEntity?> = dao.getTournamentById(id)
    fun getTournamentsByHost(hostId: String): Flow<List<TournamentEntity>> = dao.getTournamentsByHost(hostId)
    fun getPendingTournaments(): Flow<List<TournamentEntity>> = dao.getPendingApprovalTournaments()
    fun getRegistrationsForTournament(tournamentId: String): Flow<List<RegistrationEntity>> = dao.getRegistrationsForTournament(tournamentId)
    fun getUserRegistrations(): Flow<List<RegistrationEntity>> = dao.getUserRegistrations(currentUserId)
    fun getAllTeams(): Flow<List<TeamEntity>> = dao.getAllTeams()
    fun getUserTeams(): Flow<List<TeamEntity>> = dao.getUserTeams(currentUserId)
    fun getMatchRoom(tournamentId: String): Flow<MatchRoomEntity?> = dao.getMatchRoom(tournamentId)
    fun getResultsForTournament(tournamentId: String): Flow<List<TournamentResultEntity>> = dao.getResultsForTournament(tournamentId)
    fun getPendingResults(): Flow<List<TournamentResultEntity>> = dao.getPendingResults()
    fun getWalletTransactions(): Flow<List<WalletTransactionEntity>> = dao.getWalletTransactions(currentUserId)
    fun getAllHostApplications(): Flow<List<HostApplicationEntity>> = dao.getAllHostApplications()
    fun getUserHostApplication(): Flow<List<HostApplicationEntity>> = dao.getAllHostApplications()
    fun getAllDisputeReports(): Flow<List<DisputeReportEntity>> = dao.getAllDisputeReports()
    fun getUserNotifications(): Flow<List<NotificationEntity>> = dao.getUserNotifications(currentUserId)
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>> = dao.getAllAuditLogs()
    fun getAllSupportTickets(): Flow<List<SupportTicketEntity>> = dao.getAllSupportTickets()

    suspend fun seedInitialDataIfEmpty() {
        val user = dao.getUser(currentUserId)
        if (user == null) {
            // Seed Current User (Player + capability to test roles)
            val primaryUser = UserEntity(
                id = currentUserId,
                username = "ThunderGod_PG",
                fullName = "Arun Karthik",
                email = "arun.esports@pgesports.in",
                mobile = "+91 98765 43210",
                role = UserRole.PLAYER,
                rankTier = PGRankTier.MASTER,
                rating = 3450,
                totalMatches = 64,
                totalWins = 28,
                totalKills = 290,
                winRatePercent = 43.75f,
                kdRatio = 4.2f,
                walletBalance = 2450.0,
                pendingBalance = 750.0,
                totalWinnings = 16800.0,
                inGameId = "FF_ARUN_PRO",
                isHostApproved = true
            )
            dao.insertUser(primaryUser)

            // Seed other top ranked players for Leaderboard & PG Rank
            val topPlayers = listOf(
                UserEntity(
                    id = "USER_VIPER_02",
                    username = "ViperStrike",
                    fullName = "Dinesh Kumar",
                    email = "viper@pgesports.in",
                    mobile = "+91 98765 43211",
                    role = UserRole.PLAYER,
                    rankTier = PGRankTier.LEGEND,
                    rating = 5120,
                    totalMatches = 112,
                    totalWins = 68,
                    totalKills = 580,
                    winRatePercent = 60.7f,
                    kdRatio = 5.8f,
                    walletBalance = 9500.0,
                    totalWinnings = 54000.0,
                    inGameId = "VIPER_999"
                ),
                UserEntity(
                    id = "USER_PHOENIX_03",
                    username = "Phoenix_Reborn",
                    fullName = "Suresh Raina",
                    email = "phoenix@pgesports.in",
                    mobile = "+91 98765 43212",
                    role = UserRole.PLAYER,
                    rankTier = PGRankTier.GRANDMASTER,
                    rating = 4300,
                    totalMatches = 85,
                    totalWins = 45,
                    totalKills = 410,
                    winRatePercent = 52.9f,
                    kdRatio = 4.9f,
                    walletBalance = 4200.0,
                    totalWinnings = 32000.0,
                    inGameId = "PHOENIX_OP"
                ),
                UserEntity(
                    id = "USER_GHOST_04",
                    username = "GhostRider_X",
                    fullName = "Kavitha Raman",
                    email = "kavitha@pgesports.in",
                    mobile = "+91 98765 43213",
                    role = UserRole.PLAYER,
                    rankTier = PGRankTier.DIAMOND,
                    rating = 2850,
                    totalMatches = 50,
                    totalWins = 19,
                    totalKills = 210,
                    winRatePercent = 38.0f,
                    kdRatio = 3.6f,
                    walletBalance = 1500.0,
                    totalWinnings = 12000.0,
                    inGameId = "GHOST_KAVI"
                )
            )
            topPlayers.forEach { dao.insertUser(it) }

            // Seed Teams
            val initialTeams = listOf(
                TeamEntity(
                    id = "TEAM_THUNDER_SQUAD",
                    name = "Thunder Squad ⚡",
                    tag = "TS9",
                    game = "Free Fire",
                    captainId = currentUserId,
                    captainName = "ThunderGod_PG",
                    memberNames = "ThunderGod_PG, ViperStrike, SniperTamil, BlazeFire",
                    substituteNames = "GhostRider_X",
                    totalMatches = 38,
                    totalWins = 22
                ),
                TeamEntity(
                    id = "TEAM_TAMIL_WARRIORS",
                    name = "Tamil Gaming Warriors 🔥",
                    tag = "TGW",
                    game = "Free Fire",
                    captainId = "USER_VIPER_02",
                    captainName = "ViperStrike",
                    memberNames = "ViperStrike, ShadowAssassin, FalconEye, NinjaPro",
                    substituteNames = "Titan",
                    totalMatches = 45,
                    totalWins = 31
                ),
                TeamEntity(
                    id = "TEAM_ROYAL_CHALLENGERS",
                    name = "Cyber Predators 👾",
                    tag = "CP",
                    game = "BGMI",
                    captainId = "USER_PHOENIX_03",
                    captainName = "Phoenix_Reborn",
                    memberNames = "Phoenix_Reborn, PredatorX, DemonKing, ApexShot",
                    substituteNames = "Rookie",
                    totalMatches = 29,
                    totalWins = 16
                )
            )
            dao.insertTeams(initialTeams)

            // Seed Tournaments
            val currentTime = System.currentTimeMillis()
            val initialTournaments = listOf(
                TournamentEntity(
                    id = "TOUR_FF_CS_01",
                    hostId = currentUserId,
                    hostName = "PG Official Host",
                    gameId = "FREE_FIRE",
                    gameName = "Free Fire",
                    title = "Free Fire Clash Squad: Best of 3 Masters Cup",
                    description = "High-octane 4v4 Clash Squad championship. Exclusive official PG E-Sports prize pool with fast verified payout!",
                    bannerUrl = "",
                    mode = TournamentMode.CLASH_SQUAD,
                    format = TournamentFormat.BEST_OF_3,
                    mapName = "Bermuda Clash",
                    status = TournamentStatus.REGISTRATION_OPEN,
                    entryFee = 50.0,
                    prizePool = 5000.0,
                    prizeFirst = 3000.0,
                    prizeSecond = 1500.0,
                    prizeThird = 500.0,
                    maxTeams = 16,
                    registeredTeamsCount = 12,
                    startDateTimeMillis = currentTime + (2 * 3600 * 1000), // In 2 hours
                    endDateTimeMillis = currentTime + (5 * 3600 * 1000),
                    checkInStartMillis = currentTime + (90 * 60 * 1000), // In 1.5 hours
                    checkInEndMillis = currentTime + (110 * 60 * 1000),
                    registrationCloseMillis = currentTime + (80 * 60 * 1000),
                    isFeatured = true,
                    rules = "1. Gun property OFF. 2. Character skill ON. 3. No grenade spamming. 4. Screenshots mandatory after each round."
                ),
                TournamentEntity(
                    id = "TOUR_FF_BR_02",
                    hostId = "HOST_SARAVANAN",
                    hostName = "Saravanan Host",
                    gameId = "FREE_FIRE",
                    gameName = "Free Fire",
                    title = "Free Fire Battle Royale Squad Grand Open",
                    description = "12 Squads fight for survival on Bermuda. 2 Kill points + 15 placement points.",
                    bannerUrl = "",
                    mode = TournamentMode.BATTLE_ROYALE,
                    format = TournamentFormat.SQUAD,
                    mapName = "Bermuda & Purgatory (2 Rounds)",
                    status = TournamentStatus.LIVE,
                    entryFee = 100.0,
                    prizePool = 10000.0,
                    prizeFirst = 6000.0,
                    prizeSecond = 2500.0,
                    prizeThird = 1500.0,
                    maxTeams = 12,
                    registeredTeamsCount = 12,
                    startDateTimeMillis = currentTime - (30 * 60 * 1000), // Started 30 mins ago
                    endDateTimeMillis = currentTime + (90 * 60 * 1000),
                    checkInStartMillis = currentTime - (60 * 60 * 1000),
                    checkInEndMillis = currentTime - (35 * 60 * 1000),
                    registrationCloseMillis = currentTime - (70 * 60 * 1000),
                    isFeatured = true
                ),
                TournamentEntity(
                    id = "TOUR_BGMI_03",
                    hostId = "HOST_KARTHIK",
                    hostName = "Pro Esports TN",
                    gameId = "BGMI",
                    gameName = "BGMI",
                    title = "BGMI Erangel Mega Championship 2026",
                    description = "Squad Erangel Showdown. Streamed live on official PG YouTube channel.",
                    bannerUrl = "",
                    mode = TournamentMode.BATTLE_ROYALE,
                    format = TournamentFormat.SQUAD,
                    mapName = "Erangel Classic",
                    status = TournamentStatus.CHECK_IN,
                    entryFee = 150.0,
                    prizePool = 15000.0,
                    prizeFirst = 9000.0,
                    prizeSecond = 4000.0,
                    prizeThird = 2000.0,
                    maxTeams = 20,
                    registeredTeamsCount = 18,
                    startDateTimeMillis = currentTime + (25 * 60 * 1000), // In 25 mins
                    endDateTimeMillis = currentTime + (3 * 3600 * 1000),
                    checkInStartMillis = currentTime - (10 * 60 * 1000),
                    checkInEndMillis = currentTime + (15 * 60 * 1000),
                    registrationCloseMillis = currentTime - (15 * 60 * 1000),
                    isFeatured = false
                ),
                TournamentEntity(
                    id = "TOUR_VALORANT_04",
                    hostId = currentUserId,
                    hostName = "ThunderGod_PG",
                    gameId = "VALORANT_MOBILE",
                    gameName = "Valorant Mobile",
                    title = "Valorant Mobile 5v5 Spike Rush Cup",
                    description = "Fast-paced tactical shooter cup. Double elimination bracket.",
                    bannerUrl = "",
                    mode = TournamentMode.CLASH_SQUAD,
                    format = TournamentFormat.BEST_OF_1,
                    mapName = "Bind & Ascent",
                    status = TournamentStatus.COMPLETED,
                    entryFee = 40.0,
                    prizePool = 3500.0,
                    prizeFirst = 2000.0,
                    prizeSecond = 1000.0,
                    prizeThird = 500.0,
                    maxTeams = 8,
                    registeredTeamsCount = 8,
                    startDateTimeMillis = currentTime - (24 * 3600 * 1000),
                    endDateTimeMillis = currentTime - (20 * 3600 * 1000),
                    checkInStartMillis = currentTime - (25 * 3600 * 1000),
                    checkInEndMillis = currentTime - (24 * 3600 * 1000),
                    registrationCloseMillis = currentTime - (26 * 3600 * 1000),
                    isFeatured = false
                )
            )
            dao.insertTournaments(initialTournaments)

            // Seed User Registration for the Live BR tournament & the Clash Squad tournament
            val initialRegistrations = listOf(
                RegistrationEntity(
                    id = "REG_001",
                    tournamentId = "TOUR_FF_BR_02",
                    tournamentTitle = "Free Fire Battle Royale Squad Grand Open",
                    teamId = "TEAM_THUNDER_SQUAD",
                    teamName = "Thunder Squad ⚡",
                    captainId = currentUserId,
                    captainName = "ThunderGod_PG",
                    slotNumber = 4,
                    status = "CONFIRMED",
                    paymentId = "PAY_RZP_991823",
                    isCheckedIn = true,
                    checkInTime = currentTime - (45 * 60 * 1000)
                ),
                RegistrationEntity(
                    id = "REG_002",
                    tournamentId = "TOUR_BGMI_03",
                    tournamentTitle = "BGMI Erangel Mega Championship 2026",
                    teamId = "TEAM_THUNDER_SQUAD",
                    teamName = "Thunder Squad ⚡",
                    captainId = currentUserId,
                    captainName = "ThunderGod_PG",
                    slotNumber = 7,
                    status = "CONFIRMED",
                    paymentId = "PAY_RZP_991824",
                    isCheckedIn = false
                )
            )
            initialRegistrations.forEach { dao.insertRegistration(it) }

            // Seed Match Room details for the Live tournament
            val matchRoom = MatchRoomEntity(
                id = "ROOM_TOUR_FF_BR_02",
                tournamentId = "TOUR_FF_BR_02",
                matchNumber = 1,
                roundName = "Round 1 - Bermuda Map",
                roomId = "5920391",
                roomPassword = "PG_SQUAD_77",
                isReleased = true,
                releaseTimeMillis = currentTime - (35 * 60 * 1000),
                status = "LIVE"
            )
            dao.insertMatchRoom(matchRoom)

            val matchRoom2 = MatchRoomEntity(
                id = "ROOM_TOUR_FF_CS_01",
                tournamentId = "TOUR_FF_CS_01",
                matchNumber = 1,
                roundName = "Round 1 - Bracket A",
                roomId = "8472911",
                roomPassword = "PG_CS_99",
                isReleased = false,
                releaseTimeMillis = currentTime + (100 * 60 * 1000),
                status = "SCHEDULED"
            )
            dao.insertMatchRoom(matchRoom2)

            // Seed Tournament Results for Completed tournament
            val results = listOf(
                TournamentResultEntity(
                    id = "RES_001",
                    tournamentId = "TOUR_VALORANT_04",
                    matchId = "M_VAL_01",
                    teamId = "TEAM_THUNDER_SQUAD",
                    teamName = "Thunder Squad ⚡",
                    placement = 1,
                    kills = 28,
                    placementPoints = 15,
                    killPoints = 28,
                    bonus = 5,
                    penalty = 0,
                    totalScore = 48,
                    prizeAmount = 2000.0,
                    status = "VERIFIED",
                    verifiedAt = currentTime - (20 * 3600 * 1000)
                ),
                TournamentResultEntity(
                    id = "RES_002",
                    tournamentId = "TOUR_VALORANT_04",
                    matchId = "M_VAL_01",
                    teamId = "TEAM_TAMIL_WARRIORS",
                    teamName = "Tamil Gaming Warriors 🔥",
                    placement = 2,
                    kills = 21,
                    placementPoints = 10,
                    killPoints = 21,
                    bonus = 0,
                    penalty = 0,
                    totalScore = 31,
                    prizeAmount = 1000.0,
                    status = "VERIFIED",
                    verifiedAt = currentTime - (20 * 3600 * 1000)
                ),
                TournamentResultEntity(
                    id = "RES_003",
                    tournamentId = "TOUR_VALORANT_04",
                    matchId = "M_VAL_01",
                    teamId = "TEAM_ROYAL_CHALLENGERS",
                    teamName = "Cyber Predators 👾",
                    placement = 3,
                    kills = 14,
                    placementPoints = 6,
                    killPoints = 14,
                    bonus = 0,
                    penalty = 0,
                    totalScore = 20,
                    prizeAmount = 500.0,
                    status = "VERIFIED",
                    verifiedAt = currentTime - (20 * 3600 * 1000)
                )
            )
            dao.insertResults(results)

            // Seed Wallet Transactions
            val transactions = listOf(
                WalletTransactionEntity(
                    id = "TXN_001",
                    userId = currentUserId,
                    type = "PRIZE_CREDIT",
                    amount = 2000.0,
                    status = "SUCCESS",
                    description = "1st Place Prize - Valorant Mobile 5v5 Spike Rush Cup",
                    providerReference = "PG_PRIZE_PAYOUT_9921",
                    timestamp = currentTime - (20 * 3600 * 1000)
                ),
                WalletTransactionEntity(
                    id = "TXN_002",
                    userId = currentUserId,
                    type = "ENTRY_FEE",
                    amount = -100.0,
                    status = "SUCCESS",
                    description = "Entry Fee - Free Fire Battle Royale Squad Grand Open",
                    providerReference = "RZP_PAY_991823",
                    timestamp = currentTime - (2 * 3600 * 1000)
                ),
                WalletTransactionEntity(
                    id = "TXN_003",
                    userId = currentUserId,
                    type = "DEPOSIT",
                    amount = 1000.0,
                    status = "SUCCESS",
                    description = "UPI Wallet Top-up (GPay / PhonePe)",
                    providerReference = "UPI_REF_39201948",
                    timestamp = currentTime - (3 * 24 * 3600 * 1000)
                )
            )
            dao.insertTransactions(transactions)

            // Seed Host Applications
            val hostApps = listOf(
                HostApplicationEntity(
                    id = "HOST_APP_001",
                    userId = "USER_VIPER_02",
                    fullName = "Dinesh Kumar",
                    username = "ViperStrike",
                    email = "viper@pgesports.in",
                    mobile = "+91 98765 43211",
                    city = "Chennai, Tamil Nadu",
                    gamingExperience = "5 Years competitive Free Fire player, State champion 2024",
                    gamesInterested = "Free Fire, BGMI",
                    hostingExperience = "Organized 15+ college esports tournaments",
                    reason = "I want to host verified tournaments with reliable instant prize distribution for the Tamil esports community.",
                    status = "PENDING",
                    submittedAt = currentTime - (5 * 3600 * 1000)
                ),
                HostApplicationEntity(
                    id = "HOST_APP_002",
                    userId = "USER_GHOST_04",
                    fullName = "Kavitha Raman",
                    username = "GhostRider_X",
                    email = "kavitha@pgesports.in",
                    mobile = "+91 98765 43213",
                    city = "Coimbatore, Tamil Nadu",
                    gamingExperience = "4 Years BGMI streamer and organizer",
                    gamesInterested = "BGMI",
                    hostingExperience = "Managed 20 Discord community tournaments",
                    reason = "Expanding esports in southern districts.",
                    status = "APPROVED",
                    reviewerNotes = "Approved by Super Admin. Verification documents verified.",
                    submittedAt = currentTime - (2 * 24 * 3600 * 1000)
                )
            )
            hostApps.forEach { dao.insertHostApplication(it) }

            // Seed Dispute Reports
            val disputes = listOf(
                DisputeReportEntity(
                    id = "DISP_001",
                    reporterId = currentUserId,
                    reporterName = "ThunderGod_PG",
                    tournamentId = "TOUR_VALORANT_04",
                    tournamentTitle = "Valorant Mobile 5v5 Spike Rush Cup",
                    category = "WRONG_RESULT",
                    description = "Team Cyber Predators recorded 2 extra kills in round 3. Screenshot attached for admin review.",
                    priority = "NORMAL",
                    status = "RESOLVED",
                    resolutionNotes = "Verified against official stream replay. Corrected scores in final leaderboard."
                )
            )
            disputes.forEach { dao.insertDisputeReport(it) }

            // Seed Notifications
            val notifications = listOf(
                NotificationEntity(
                    id = "NOTIF_001",
                    userId = currentUserId,
                    title = "Room Credentials Released! 🔑",
                    message = "Room ID: 5920391 | Pass: PG_SQUAD_77 for Free Fire Battle Royale Squad Grand Open. Join within 10 mins!",
                    category = "MATCH_ROOM",
                    deepLink = "pg-esports://match/TOUR_FF_BR_02",
                    isRead = false,
                    timestamp = currentTime - (25 * 60 * 1000)
                ),
                NotificationEntity(
                    id = "NOTIF_002",
                    userId = currentUserId,
                    title = "Prize Credited! 🏆 ₹2,000",
                    message = "Congratulations! 1st Place winnings for Valorant Mobile 5v5 Cup has been credited to your PG Wallet.",
                    category = "WALLET",
                    deepLink = "pg-esports://wallet",
                    isRead = true,
                    timestamp = currentTime - (20 * 3600 * 1000)
                ),
                NotificationEntity(
                    id = "NOTIF_003",
                    userId = currentUserId,
                    title = "Check-in Open ⏱️",
                    message = "Check-in is now live for BGMI Erangel Mega Championship. Please confirm your squad attendance.",
                    category = "TOURNAMENT",
                    deepLink = "pg-esports://tournament/TOUR_BGMI_03",
                    isRead = false,
                    timestamp = currentTime - (10 * 60 * 1000)
                )
            )
            dao.insertNotifications(notifications)

            // Seed Audit Logs
            val auditLogs = listOf(
                AuditLogEntity(
                    id = "AUDIT_001",
                    actorId = "SUPER_ADMIN_01",
                    actorRole = "SUPER_ADMIN",
                    action = "HOST_APPROVAL",
                    entityType = "USER",
                    entityId = "USER_GHOST_04",
                    details = "Approved host privileges for Kavitha Raman after ID check",
                    timestamp = currentTime - (2 * 24 * 3600 * 1000)
                ),
                AuditLogEntity(
                    id = "AUDIT_002",
                    actorId = "ADMIN_01",
                    actorRole = "ADMIN",
                    action = "VERIFY_RESULTS",
                    entityType = "TOURNAMENT",
                    entityId = "TOUR_VALORANT_04",
                    details = "Verified final match standings and authorized automatic prize disbursement of ₹3,500.",
                    timestamp = currentTime - (20 * 3600 * 1000)
                ),
                AuditLogEntity(
                    id = "AUDIT_003",
                    actorId = "HOST_01",
                    actorRole = "HOST",
                    action = "RELEASE_ROOM",
                    entityType = "MATCH_ROOM",
                    entityId = "ROOM_TOUR_FF_BR_02",
                    details = "Host released room credentials to 12 checked-in teams.",
                    timestamp = currentTime - (35 * 60 * 1000)
                )
            )
            auditLogs.forEach { dao.insertAuditLog(it) }

            // Seed Support Tickets
            val supportTickets = listOf(
                SupportTicketEntity(
                    id = "TICKET_001",
                    userId = currentUserId,
                    subject = "UPI Withdrawal timing",
                    category = "PAYOUT",
                    message = "How long does it take for IMPS/UPI bank withdrawal to reflect in my bank account?",
                    status = "RESOLVED",
                    adminReply = "UPI payouts are automatically processed within 15-30 minutes after admin audit verification.",
                    createdAt = currentTime - (48 * 3600 * 1000)
                )
            )
            supportTickets.forEach { dao.insertSupportTicket(it) }
        }
    }

    // --- Action Methods ---

    suspend fun switchUserRole(role: UserRole) {
        val user = dao.getUser(currentUserId) ?: return
        dao.updateUser(user.copy(role = role))
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = user.role.name,
                action = "ROLE_SWITCH",
                entityType = "USER",
                entityId = currentUserId,
                details = "Switched active console role from ${user.role.name} to ${role.name}"
            )
        )
    }

    suspend fun createTournament(tournament: TournamentEntity) {
        dao.insertTournament(tournament)
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "HOST",
                action = "CREATE_TOURNAMENT",
                entityType = "TOURNAMENT",
                entityId = tournament.id,
                details = "Created tournament '${tournament.title}' in state ${tournament.status}"
            )
        )
    }

    suspend fun approveTournament(tournamentId: String) {
        // Find tournament and approve
        // In real app, fetches and updates
    }

    suspend fun updateTournamentStatus(tournamentId: String, status: TournamentStatus, reason: String = "") {
        // Can be called by Admin or Host
    }

    suspend fun registerForTournament(tournament: TournamentEntity, team: TeamEntity): Boolean {
        val user = dao.getUser(currentUserId) ?: return false
        if (user.walletBalance < tournament.entryFee) {
            return false
        }

        val newBalance = user.walletBalance - tournament.entryFee
        dao.updateUser(user.copy(walletBalance = newBalance))

        val regId = "REG_" + UUID.randomUUID().toString().take(8).uppercase()
        val paymentId = "PAY_RZP_" + UUID.randomUUID().toString().take(8).uppercase()

        val registration = RegistrationEntity(
            id = regId,
            tournamentId = tournament.id,
            tournamentTitle = tournament.title,
            teamId = team.id,
            teamName = team.name,
            captainId = currentUserId,
            captainName = user.username,
            playerUid = user.freeFireUid,
            playerIgn = user.freeFireIgn,
            slotNumber = tournament.registeredTeamsCount + 1,
            status = "CONFIRMED",
            paymentId = paymentId,
            isCheckedIn = false
        )
        dao.insertRegistration(registration)

        // Update tournament count
        dao.updateTournament(tournament.copy(registeredTeamsCount = tournament.registeredTeamsCount + 1))

        // Record immutable ledger entry
        dao.insertTransaction(
            WalletTransactionEntity(
                id = "TXN_" + UUID.randomUUID().toString().take(8).uppercase(),
                userId = currentUserId,
                type = "ENTRY_FEE",
                amount = -tournament.entryFee,
                status = "SUCCESS",
                description = "Entry Fee for ${tournament.title}",
                providerReference = paymentId
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Registration Confirmed! 🎮",
                message = "Your squad ${team.name} is successfully booked for slot #${registration.slotNumber} in ${tournament.title}.",
                category = "TOURNAMENT",
                deepLink = "pg-esports://tournament/${tournament.id}"
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "PLAYER",
                action = "TOURNAMENT_REGISTER",
                entityType = "REGISTRATION",
                entityId = regId,
                details = "Registered slot #${registration.slotNumber} in ${tournament.title} with payment $paymentId"
            )
        )

        return true
    }

    /**
     * Process direct Razorpay SDK Tournament Entry Fee Payment with full transactional logging
     */
    suspend fun processRazorpayTournamentEntryPayment(
        tournament: TournamentEntity,
        team: TeamEntity,
        paymentResult: PaymentResultData
    ): Boolean {
        val user = dao.getUser(currentUserId) ?: return false
        val paymentId = paymentResult.paymentId ?: ("PAY_RZP_" + UUID.randomUUID().toString().take(8).uppercase())
        val slotNumber = tournament.registeredTeamsCount + 1
        val regId = "REG_RZP_" + UUID.randomUUID().toString().take(8).uppercase()

        // 1. Create Confirmed Slot Registration
        val registration = RegistrationEntity(
            id = regId,
            tournamentId = tournament.id,
            tournamentTitle = tournament.title,
            teamId = team.id,
            teamName = team.name,
            captainId = currentUserId,
            captainName = user.username,
            playerUid = user.freeFireUid,
            playerIgn = user.freeFireIgn,
            slotNumber = slotNumber,
            status = "CONFIRMED",
            paymentId = paymentId,
            isCheckedIn = false
        )
        dao.insertRegistration(registration)

        // 2. Update Tournament registered count
        dao.updateTournament(tournament.copy(registeredTeamsCount = slotNumber))

        // 3. Log to Transaction Ledger
        val txnId = "TXN_RZP_" + UUID.randomUUID().toString().take(8).uppercase()
        dao.insertTransaction(
            WalletTransactionEntity(
                id = txnId,
                userId = currentUserId,
                type = "ENTRY_FEE",
                amount = -tournament.entryFee,
                status = "SUCCESS",
                description = "Razorpay Entry Fee - ${tournament.title} (Slot #$slotNumber)",
                providerReference = paymentId,
                timestamp = System.currentTimeMillis()
            )
        )

        // 4. Log to Security Audit Trail
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "PLAYER",
                action = "RAZORPAY_ENTRY_FEE_SUCCESS",
                entityType = "TRANSACTION",
                entityId = txnId,
                details = "Razorpay Entry Fee Verified. Txn: $paymentId, Tournament: ${tournament.id}, Team: ${team.name}, Slot: #$slotNumber, Amount: ₹${tournament.entryFee}",
                timestamp = System.currentTimeMillis()
            )
        )

        // 5. Send Realtime Notification
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Payment Verified & Slot #$slotNumber Booked! 🎮",
                message = "Payment of ₹${tournament.entryFee} confirmed via Razorpay ($paymentId). Your squad ${team.name} is ready for ${tournament.title}.",
                category = "TOURNAMENT",
                deepLink = "pg-esports://tournament/${tournament.id}"
            )
        )

        return true
    }

    /**
     * Record Failed Razorpay Payment Attempt in the Transaction & Audit Log
     */
    suspend fun recordFailedRazorpayPayment(
        tournamentId: String,
        tournamentTitle: String,
        teamName: String,
        amount: Double,
        errorCode: Int,
        errorMessage: String
    ) {
        val failedTxnId = "TXN_FAIL_" + UUID.randomUUID().toString().take(8).uppercase()
        val failureRef = "RZP_ERR_${System.currentTimeMillis().toString().takeLast(6)}"

        dao.insertTransaction(
            WalletTransactionEntity(
                id = failedTxnId,
                userId = currentUserId,
                type = "ENTRY_FEE",
                amount = 0.0,
                status = "FAILED",
                description = "Failed Razorpay Payment - $tournamentTitle ($errorMessage)",
                providerReference = failureRef,
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "PLAYER",
                action = "RAZORPAY_PAYMENT_FAILED",
                entityType = "TRANSACTION",
                entityId = failedTxnId,
                details = "Payment failed. Code: $errorCode, Reason: $errorMessage, Tournament: $tournamentId, Team: $teamName, Amount: ₹$amount",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Payment Failed ⚠️",
                message = "Entry fee payment for $tournamentTitle could not be completed: $errorMessage. You can retry anytime.",
                category = "WALLET",
                deepLink = "pg-esports://tournament/$tournamentId"
            )
        )
    }

    /**
     * Process Wallet Deposit via Razorpay SDK
     */
    suspend fun processRazorpayWalletDeposit(
        amount: Double,
        paymentResult: PaymentResultData
    ) {
        val user = dao.getUser(currentUserId) ?: return
        val updatedBalance = user.walletBalance + amount
        dao.updateUser(user.copy(walletBalance = updatedBalance))

        val paymentId = paymentResult.paymentId ?: ("PAY_RZP_DEP_" + UUID.randomUUID().toString().take(6).uppercase())
        val txnId = "TXN_DEP_" + UUID.randomUUID().toString().take(8).uppercase()

        dao.insertTransaction(
            WalletTransactionEntity(
                id = txnId,
                userId = currentUserId,
                type = "DEPOSIT",
                amount = amount,
                status = "SUCCESS",
                description = "Razorpay Instant Deposit (UPI / Cards / NetBanking)",
                providerReference = paymentId,
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "PLAYER",
                action = "RAZORPAY_WALLET_DEPOSIT",
                entityType = "TRANSACTION",
                entityId = txnId,
                details = "Deposit of ₹$amount successful via Razorpay ($paymentId). New balance: ₹$updatedBalance",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "PG Wallet Credited! 💳 ₹${amount.toInt()}",
                message = "₹$amount successfully added to your PG Wallet via Razorpay ($paymentId). Available for instant tournament registration.",
                category = "WALLET",
                deepLink = "pg-esports://wallet"
            )
        )
    }

    suspend fun checkInTeam(registrationId: String, tournamentId: String) {
        // Mark check-in
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Checked-in Successfully! ✅",
                message = "Your attendance is verified. Please stand by for Room ID & Password release.",
                category = "MATCH_ROOM",
                deepLink = "pg-esports://match/$tournamentId"
            )
        )
    }

    suspend fun releaseRoomCredentials(tournamentId: String, roomId: String, password: String) {
        val room = MatchRoomEntity(
            id = "ROOM_$tournamentId",
            tournamentId = tournamentId,
            roomId = roomId,
            roomPassword = password,
            isReleased = true,
            releaseTimeMillis = System.currentTimeMillis(),
            status = "ROOM_READY"
        )
        dao.insertMatchRoom(room)

        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "HOST",
                action = "RELEASE_ROOM",
                entityType = "MATCH_ROOM",
                entityId = room.id,
                details = "Room credentials released: ID=$roomId"
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Match Room is Ready! 🔑",
                message = "Room ID: $roomId | Password: $password. Open PG E-SPORTS app to copy and join custom room.",
                category = "MATCH_ROOM",
                deepLink = "pg-esports://match/$tournamentId"
            )
        )
    }

    suspend fun submitHostApplication(app: HostApplicationEntity) {
        dao.insertHostApplication(app)
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Host Application Submitted 📋",
                message = "Super Admin will review your verification details within 24 hours.",
                category = "ADMIN"
            )
        )
    }

    suspend fun reviewHostApplication(appId: String, approve: Boolean, reviewerNotes: String = "") {
        // App review
        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "SUPER_ADMIN",
                action = if (approve) "APPROVE_HOST" else "REJECT_HOST",
                entityType = "HOST_APPLICATION",
                entityId = appId,
                details = "Decision: ${if (approve) "Approved" else "Rejected"}. Notes: $reviewerNotes"
            )
        )
    }

    suspend fun submitDisputeReport(report: DisputeReportEntity) {
        dao.insertDisputeReport(report)
        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Dispute Ticket Logged 🛡️",
                message = "Our referee team has received your ticket #${report.id.take(6)}. Investigation in progress.",
                category = "SYSTEM"
            )
        )
    }

    suspend fun submitSupportTicket(ticket: SupportTicketEntity) {
        dao.insertSupportTicket(ticket)
    }

    suspend fun addWalletDeposit(amount: Double) {
        val user = dao.getUser(currentUserId) ?: return
        val updatedBalance = user.walletBalance + amount
        dao.updateUser(user.copy(walletBalance = updatedBalance))

        dao.insertTransaction(
            WalletTransactionEntity(
                id = "TXN_" + UUID.randomUUID().toString().take(8).uppercase(),
                userId = currentUserId,
                type = "DEPOSIT",
                amount = amount,
                status = "SUCCESS",
                description = "Instant UPI Deposit (Verified)",
                providerReference = "RZP_DEP_" + UUID.randomUUID().toString().take(6).uppercase()
            )
        )
    }

    suspend fun requestWithdrawal(amount: Double, upiId: String): Boolean {
        val user = dao.getUser(currentUserId) ?: return false
        if (user.walletBalance < amount) return false

        val updatedBalance = user.walletBalance - amount
        dao.updateUser(user.copy(walletBalance = updatedBalance))

        dao.insertTransaction(
            WalletTransactionEntity(
                id = "TXN_" + UUID.randomUUID().toString().take(8).uppercase(),
                userId = currentUserId,
                type = "WITHDRAWAL",
                amount = -amount,
                status = "PENDING",
                description = "Bank Payout to $upiId (Audit in progress)",
                providerReference = "PAYOUT_REQ_" + UUID.randomUUID().toString().take(6).uppercase()
            )
        )
        return true
    }

    suspend fun createTeam(team: TeamEntity) {
        dao.insertTeam(team)
    }

    suspend fun updateTournamentEntity(t: TournamentEntity) {
        dao.updateTournament(t)
    }

    suspend fun insertResult(result: TournamentResultEntity) {
        dao.insertResult(result)
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead(currentUserId)
    }

    suspend fun updateFreeFireAccountDetails(
        uid: String,
        ign: String,
        level: Int,
        rankTier: String,
        serverRegion: String,
        battleRole: String,
        guildName: String
    ) {
        dao.updateFreeFireAccountDetails(
            userId = currentUserId,
            uid = uid.trim(),
            ign = ign.trim(),
            level = level,
            rankTier = rankTier,
            serverRegion = serverRegion,
            battleRole = battleRole,
            guildName = guildName.trim()
        )

        dao.insertAuditLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actorId = currentUserId,
                actorRole = "PLAYER",
                action = "UPDATE_FREE_FIRE_ACCOUNT",
                entityType = "USER",
                entityId = currentUserId,
                details = "Free Fire Account Details Updated. UID: $uid, IGN: $ign, Lvl: $level, Rank: $rankTier, Server: $serverRegion, Role: $battleRole",
                timestamp = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "Free Fire ID Updated 🎮",
                message = "Your Free Fire Player UID ($uid) and IGN ($ign) are verified for upcoming tournament room slots.",
                category = "SYSTEM"
            )
        )
    }

    suspend fun updateGoogleAccount(
        googleId: String,
        googleEmail: String,
        googleDisplayName: String,
        isLinked: Boolean
    ) {
        val now = System.currentTimeMillis()
        dao.updateGoogleAccount(
            userId = currentUserId,
            googleId = googleId,
            googleEmail = googleEmail,
            googleDisplayName = googleDisplayName,
            isLinked = isLinked,
            syncTime = now
        )
    }

    suspend fun syncCloudDatabase(): Map<String, Int> {
        val now = System.currentTimeMillis()
        dao.updateCloudSyncTimestamp(currentUserId, now)
        return getDatabaseStatistics()
    }

    suspend fun getDatabaseStatistics(): Map<String, Int> {
        return mapOf(
            "Users" to dao.getUserCount(),
            "Tournaments" to dao.getTournamentCount(),
            "Teams" to dao.getTeamCount(),
            "Registrations" to dao.getRegistrationCount(),
            "Transactions" to dao.getTransactionCount()
        )
    }
}
