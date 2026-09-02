package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.localization.AppLanguage
import com.example.data.model.*
import com.example.data.repository.PGEsportsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

data class UiNotification(
    val message: String,
    val isError: Boolean = false,
    val id: String = UUID.randomUUID().toString()
)

class PGEsportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PGEsportsRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = PGEsportsRepository(db.appDao())
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    // App Language State
    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.TAMIL else AppLanguage.ENGLISH
    }

    // Current User
    val currentUser: StateFlow<UserEntity?> = repository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Tournaments Flow
    val allTournaments: StateFlow<List<TournamentEntity>> = repository.getAllTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter & Search State for Tournaments
    val searchQuery = MutableStateFlow("")
    val selectedGameFilter = MutableStateFlow("ALL")
    val selectedStatusFilter = MutableStateFlow("ALL")

    val filteredTournaments: StateFlow<List<TournamentEntity>> = combine(
        allTournaments,
        searchQuery,
        selectedGameFilter,
        selectedStatusFilter
    ) { tournaments, query, gameFilter, statusFilter ->
        tournaments.filter { tournament ->
            val matchesQuery = query.isEmpty() ||
                    tournament.title.contains(query, ignoreCase = true) ||
                    tournament.gameName.contains(query, ignoreCase = true) ||
                    tournament.mapName.contains(query, ignoreCase = true)

            val matchesGame = gameFilter == "ALL" || tournament.gameId.equals(gameFilter, ignoreCase = true)

            val matchesStatus = when (statusFilter) {
                "ALL" -> true
                "LIVE" -> tournament.status == TournamentStatus.LIVE
                "REGISTRATION_OPEN" -> tournament.status == TournamentStatus.REGISTRATION_OPEN
                "CHECK_IN" -> tournament.status == TournamentStatus.CHECK_IN
                "COMPLETED" -> tournament.status == TournamentStatus.COMPLETED
                else -> true
            }

            matchesQuery && matchesGame && matchesStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Other core data flows
    val usersByRank: StateFlow<List<UserEntity>> = repository.getAllUsersByRank()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userRegistrations: StateFlow<List<RegistrationEntity>> = repository.getUserRegistrations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTeams: StateFlow<List<TeamEntity>> = repository.getUserTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTeams: StateFlow<List<TeamEntity>> = repository.getAllTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val walletTransactions: StateFlow<List<WalletTransactionEntity>> = repository.getWalletTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.getUserNotifications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hostApplications: StateFlow<List<HostApplicationEntity>> = repository.getAllHostApplications()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val disputeReports: StateFlow<List<DisputeReportEntity>> = repository.getAllDisputeReports()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditLogEntity>> = repository.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicketEntity>> = repository.getAllSupportTickets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingTournaments: StateFlow<List<TournamentEntity>> = repository.getPendingTournaments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Toast/Snackbar notifications
    private val _uiEvent = MutableSharedFlow<UiNotification>()
    val uiEvent = _uiEvent.asSharedFlow()

    // --- Actions ---

    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            repository.switchUserRole(role)
            _uiEvent.emit(UiNotification("Active role switched to ${role.name}"))
        }
    }

    fun registerTournament(tournament: TournamentEntity, team: TeamEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val success = repository.registerForTournament(tournament, team)
            if (success) {
                _uiEvent.emit(UiNotification("Registration & Payment Successful! Slot booked."))
                onSuccess()
            } else {
                _uiEvent.emit(UiNotification("Insufficient wallet balance! Please add funds.", isError = true))
            }
        }
    }

    fun checkInTeam(registration: RegistrationEntity) {
        viewModelScope.launch {
            repository.checkInTeam(registration.id, registration.tournamentId)
            _uiEvent.emit(UiNotification("Check-In Confirmed for ${registration.teamName}!"))
        }
    }

    fun releaseRoomCredentials(tournamentId: String, roomId: String, password: String) {
        viewModelScope.launch {
            repository.releaseRoomCredentials(tournamentId, roomId, password)
            _uiEvent.emit(UiNotification("Match Room credentials released to verified teams!"))
        }
    }

    fun addDeposit(amount: Double) {
        viewModelScope.launch {
            repository.addWalletDeposit(amount)
            _uiEvent.emit(UiNotification("₹$amount added to PG Wallet via UPI!"))
        }
    }

    fun requestWithdrawal(amount: Double, upiId: String) {
        viewModelScope.launch {
            val success = repository.requestWithdrawal(amount, upiId)
            if (success) {
                _uiEvent.emit(UiNotification("Withdrawal of ₹$amount initiated to $upiId."))
            } else {
                _uiEvent.emit(UiNotification("Insufficient balance for withdrawal!", isError = true))
            }
        }
    }

    fun createTeam(name: String, tag: String, game: String, members: String) {
        viewModelScope.launch {
            val team = TeamEntity(
                id = "TEAM_" + UUID.randomUUID().toString().take(6).uppercase(),
                name = name,
                tag = tag,
                game = game,
                captainId = repository.currentUserId,
                captainName = currentUser.value?.username ?: "Captain",
                memberNames = members
            )
            repository.createTeam(team)
            _uiEvent.emit(UiNotification("Squad '$name' created successfully!"))
        }
    }

    fun applyAsHost(
        fullName: String,
        email: String,
        mobile: String,
        city: String,
        gamingExp: String,
        interestedGames: String,
        hostingExp: String,
        reason: String
    ) {
        viewModelScope.launch {
            val app = HostApplicationEntity(
                id = "HOST_APP_" + UUID.randomUUID().toString().take(6).uppercase(),
                userId = repository.currentUserId,
                fullName = fullName,
                username = currentUser.value?.username ?: "Player",
                email = email,
                mobile = mobile,
                city = city,
                gamingExperience = gamingExp,
                gamesInterested = interestedGames,
                hostingExperience = hostingExp,
                reason = reason,
                status = "PENDING"
            )
            repository.submitHostApplication(app)
            _uiEvent.emit(UiNotification("Host verification application submitted for Super Admin review."))
        }
    }

    fun createTournament(
        title: String,
        gameId: String,
        gameName: String,
        mode: TournamentMode,
        format: TournamentFormat,
        mapName: String,
        entryFee: Double,
        prizePool: Double,
        maxTeams: Int,
        rules: String
    ) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val tournament = TournamentEntity(
                id = "TOUR_" + UUID.randomUUID().toString().take(8).uppercase(),
                hostId = repository.currentUserId,
                hostName = currentUser.value?.username ?: "Host",
                gameId = gameId,
                gameName = gameName,
                title = title,
                description = "Official tournament hosted via PG E-SPORTS 2.0 Host Hub.",
                bannerUrl = "",
                mode = mode,
                format = format,
                mapName = mapName,
                status = TournamentStatus.REGISTRATION_OPEN,
                entryFee = entryFee,
                prizePool = prizePool,
                prizeFirst = prizePool * 0.6,
                prizeSecond = prizePool * 0.3,
                prizeThird = prizePool * 0.1,
                maxTeams = maxTeams,
                registeredTeamsCount = 0,
                startDateTimeMillis = now + (3 * 3600 * 1000),
                endDateTimeMillis = now + (6 * 3600 * 1000),
                checkInStartMillis = now + (2 * 3600 * 1000),
                checkInEndMillis = now + (160 * 60 * 1000),
                registrationCloseMillis = now + (150 * 60 * 1000),
                rules = rules
            )
            repository.createTournament(tournament)
            _uiEvent.emit(UiNotification("Tournament '$title' created & published!"))
        }
    }

    fun submitMatchResult(
        tournamentId: String,
        teamName: String,
        placement: Int,
        kills: Int,
        score: Int,
        proofUrl: String = ""
    ) {
        viewModelScope.launch {
            val result = TournamentResultEntity(
                id = "RES_" + UUID.randomUUID().toString().take(6).uppercase(),
                tournamentId = tournamentId,
                matchId = "MATCH_M1",
                teamId = "TEAM_DEMO",
                teamName = teamName,
                placement = placement,
                kills = kills,
                placementPoints = 15,
                killPoints = kills * 2,
                totalScore = score,
                status = "UNDER_REVIEW",
                proofImageUrl = proofUrl
            )
            repository.insertResult(result)
            _uiEvent.emit(UiNotification("Match result & proof submitted for admin verification."))
        }
    }

    fun submitDispute(
        tournamentId: String,
        tournamentTitle: String,
        category: String,
        description: String
    ) {
        viewModelScope.launch {
            val report = DisputeReportEntity(
                id = "DISP_" + UUID.randomUUID().toString().take(6).uppercase(),
                reporterId = repository.currentUserId,
                reporterName = currentUser.value?.username ?: "Player",
                tournamentId = tournamentId,
                tournamentTitle = tournamentTitle,
                category = category,
                description = description,
                status = "OPEN"
            )
            repository.submitDisputeReport(report)
            _uiEvent.emit(UiNotification("Dispute ticket logged with referee desk."))
        }
    }

    fun submitSupportTicket(subject: String, category: String, message: String) {
        viewModelScope.launch {
            val ticket = SupportTicketEntity(
                id = "TICKET_" + UUID.randomUUID().toString().take(6).uppercase(),
                userId = repository.currentUserId,
                subject = subject,
                category = category,
                message = message,
                status = "OPEN"
            )
            repository.submitSupportTicket(ticket)
            _uiEvent.emit(UiNotification("Support request received. Ref: #${ticket.id.take(8)}"))
        }
    }

    fun markNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    fun getMatchRoom(tournamentId: String): Flow<MatchRoomEntity?> {
        return repository.getMatchRoom(tournamentId)
    }

    fun getTournamentResults(tournamentId: String): Flow<List<TournamentResultEntity>> {
        return repository.getResultsForTournament(tournamentId)
    }

    fun getTournamentRegistrations(tournamentId: String): Flow<List<RegistrationEntity>> {
        return repository.getRegistrationsForTournament(tournamentId)
    }

    // --- Google Account & Database Sync ---
    private val _databaseStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val databaseStats: StateFlow<Map<String, Int>> = _databaseStats.asStateFlow()

    fun linkGoogleAccount(googleId: String, email: String, displayName: String) {
        viewModelScope.launch {
            repository.updateGoogleAccount(
                googleId = googleId,
                googleEmail = email,
                googleDisplayName = displayName,
                isLinked = true
            )
            _uiEvent.emit(UiNotification("Google ID successfully linked with PG Database! ($email)"))
            fetchDatabaseStats()
        }
    }

    fun unlinkGoogleAccount() {
        viewModelScope.launch {
            repository.updateGoogleAccount(
                googleId = "",
                googleEmail = "",
                googleDisplayName = "",
                isLinked = false
            )
            _uiEvent.emit(UiNotification("Google account unlinked."))
            fetchDatabaseStats()
        }
    }

    fun syncDatabaseWithCloud() {
        viewModelScope.launch {
            val stats = repository.syncCloudDatabase()
            _databaseStats.value = stats
            _uiEvent.emit(UiNotification("Local Room Database synced with Google Cloud DB! ☁️"))
        }
    }

    fun fetchDatabaseStats() {
        viewModelScope.launch {
            _databaseStats.value = repository.getDatabaseStatistics()
        }
    }
}
