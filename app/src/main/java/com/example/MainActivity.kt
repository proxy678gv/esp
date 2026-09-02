package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.localization.AppLanguage
import com.example.data.localization.PGStrings
import com.example.data.model.TournamentEntity
import com.example.data.model.UserRole
import com.example.ui.components.PGGlassCard
import com.example.ui.components.PGRankBadge
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import com.example.payment.RazorpayIntegrationService
import com.example.payment.RazorpayPaymentManager
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener

enum class Screen {
    HOME,
    FREE_FIRE_HUB,
    TOURNAMENTS,
    RANK,
    TEAMS,
    WALLET,
    HOST_HUB,
    ADMIN_CONSOLE,
    NOTIFICATIONS,
    SUPPORT,
    PROFILE,
    TOURNAMENT_DETAIL,
    MATCH_ROOM,
    PDF_DOC
}

class MainActivity : ComponentActivity(), PaymentResultWithDataListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RazorpayIntegrationService.getInstance().initializeSdk(this, isTestMode = true)
        RazorpayPaymentManager.preload(this)
        enableEdgeToEdge()
        val incomingIntent = intent
        setContent {
            PGTheme {
                PGEsportsApp(intent = incomingIntent)
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        RazorpayIntegrationService.getInstance().onPaymentSuccessCallback(razorpayPaymentId, paymentData)
        RazorpayPaymentManager.handlePaymentSuccess(razorpayPaymentId, paymentData)
    }

    override fun onPaymentError(errorCode: Int, errorDescription: String?, paymentData: PaymentData?) {
        RazorpayIntegrationService.getInstance().onPaymentErrorCallback(errorCode, errorDescription, paymentData)
        RazorpayPaymentManager.handlePaymentError(errorCode, errorDescription, paymentData)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PGEsportsApp(
    viewModel: PGEsportsViewModel = viewModel(),
    intent: android.content.Intent? = null
) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    var selectedTournament by remember { mutableStateOf<TournamentEntity?>(null) }
    var activeMatchTournamentId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(intent) {
        if (intent != null && intent.getBooleanExtra("ACTION_NAVIGATE_MATCH_ROOM", false)) {
            val tourId = intent.getStringExtra(com.example.notification.MatchNotificationHelper.EXTRA_TOURNAMENT_ID)
            if (!tourId.isNullOrEmpty()) {
                activeMatchTournamentId = tourId
                currentScreen = Screen.MATCH_ROOM
            }
        }
    }

    val user by viewModel.currentUser.collectAsState()
    val language by viewModel.language.collectAsState()
    val allTournaments by viewModel.allTournaments.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = remember(notifications) { notifications.count { !it.isRead } }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = data.visuals.message,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        },
        topBar = {
            if (currentScreen != Screen.TOURNAMENT_DETAIL && currentScreen != Screen.MATCH_ROOM) {
                Surface(
                    color = DarkBackground,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // App Brand Logo & Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { currentScreen = Screen.HOME }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(NeonPurpleDark, DarkSurfaceVariant, NeonGreenDark)
                                        )
                                    )
                                    .padding(1.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "PG",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = NeonGreen
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "PG E-SPORTS",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "2.0",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = NeonGreen
                                    )
                                }
                                Text(
                                    text = PGStrings.appTagline(language),
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Top Action Icons (Role indicator, Language toggle, Notifications)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Free Fire 4K Booyah Hub Quick Launch
                            Surface(
                                modifier = Modifier
                                    .clickable { currentScreen = Screen.FREE_FIRE_HUB }
                                    .padding(end = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = CyberOrange.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberOrange)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🔥 FF HUB",
                                        color = CyberOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            // Language Switcher Badge
                            Surface(
                                modifier = Modifier
                                    .clickable { viewModel.toggleLanguage() }
                                    .padding(end = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = DarkSurfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = if (language == AppLanguage.ENGLISH) "EN" else "தமிழ்",
                                    color = ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            // Notification Bell with Badge
                            IconButton(onClick = { currentScreen = Screen.NOTIFICATIONS }) {
                                BadgedBox(
                                    badge = {
                                        if (unreadCount > 0) {
                                            Badge(
                                                containerColor = CyberRed,
                                                contentColor = TextPrimary
                                            ) {
                                                Text("$unreadCount")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = if (currentScreen == Screen.NOTIFICATIONS) NeonGreen else TextPrimary
                                    )
                                }
                            }

                            // PDF Spec Doc Viewer Quick Action
                            IconButton(onClick = { currentScreen = Screen.PDF_DOC }) {
                                Icon(
                                    Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF Product Spec",
                                    tint = if (currentScreen == Screen.PDF_DOC) NeonPurpleLight else TextSecondary
                                )
                            }

                            // Admin / Super Admin console quick-access
                            IconButton(onClick = { currentScreen = Screen.ADMIN_CONSOLE }) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Console",
                                    tint = if (currentScreen == Screen.ADMIN_CONSOLE) CyberGold else TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (currentScreen != Screen.TOURNAMENT_DETAIL && currentScreen != Screen.MATCH_ROOM) {
                Surface(
                    color = DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavTabItem(
                            icon = Icons.Default.Home,
                            label = PGStrings.home(language),
                            isSelected = currentScreen == Screen.HOME,
                            onClick = { currentScreen = Screen.HOME }
                        )

                        NavTabItem(
                            icon = Icons.Default.LocalFireDepartment,
                            label = "Free Fire",
                            isSelected = currentScreen == Screen.FREE_FIRE_HUB,
                            onClick = { currentScreen = Screen.FREE_FIRE_HUB }
                        )

                        NavTabItem(
                            icon = Icons.Default.EmojiEvents,
                            label = PGStrings.tournaments(language),
                            isSelected = currentScreen == Screen.TOURNAMENT_DETAIL || currentScreen == Screen.TOURNAMENTS,
                            onClick = { currentScreen = Screen.TOURNAMENTS }
                        )

                        NavTabItem(
                            icon = Icons.Default.MilitaryTech,
                            label = PGStrings.pgRank(language),
                            isSelected = currentScreen == Screen.RANK,
                            onClick = { currentScreen = Screen.RANK }
                        )

                        NavTabItem(
                            icon = Icons.Default.AccountBalanceWallet,
                            label = PGStrings.wallet(language),
                            isSelected = currentScreen == Screen.WALLET,
                            onClick = { currentScreen = Screen.WALLET }
                        )

                        NavTabItem(
                            icon = Icons.Default.Person,
                            label = "Profile",
                            isSelected = currentScreen == Screen.PROFILE,
                            onClick = { currentScreen = Screen.PROFILE }
                        )
                    }
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTournaments = { currentScreen = Screen.TOURNAMENTS },
                    onNavigateToFreeFireHub = { currentScreen = Screen.FREE_FIRE_HUB },
                    onSelectTournament = { tour ->
                        selectedTournament = tour
                        currentScreen = Screen.TOURNAMENT_DETAIL
                    },
                    onNavigateToMatchRoom = { tourId ->
                        activeMatchTournamentId = tourId
                        currentScreen = Screen.MATCH_ROOM
                    },
                    onNavigateToWallet = { currentScreen = Screen.WALLET },
                    onNavigateToRank = { currentScreen = Screen.RANK }
                )

                Screen.FREE_FIRE_HUB -> FreeFireHubScreen(
                    viewModel = viewModel,
                    onNavigateToTournaments = { currentScreen = Screen.TOURNAMENTS },
                    onBack = { currentScreen = Screen.HOME }
                )

                Screen.TOURNAMENTS -> TournamentsScreen(
                    viewModel = viewModel,
                    onSelectTournament = { tour ->
                        selectedTournament = tour
                        currentScreen = Screen.TOURNAMENT_DETAIL
                    }
                )

                Screen.TOURNAMENT_DETAIL -> {
                    selectedTournament?.let { tour ->
                        TournamentDetailScreen(
                            tournament = tour,
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.TOURNAMENTS },
                            onNavigateToMatchRoom = {
                                activeMatchTournamentId = tour.id
                                currentScreen = Screen.MATCH_ROOM
                            }
                        )
                    } ?: run {
                        currentScreen = Screen.TOURNAMENTS
                    }
                }

                Screen.MATCH_ROOM -> {
                    val targetTour = allTournaments.find { it.id == activeMatchTournamentId } ?: selectedTournament ?: allTournaments.firstOrNull()
                    if (targetTour != null) {
                        MatchRoomScreen(
                            tournament = targetTour,
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.HOME }
                        )
                    } else {
                        currentScreen = Screen.HOME
                    }
                }

                Screen.RANK -> PGRankScreen(viewModel = viewModel)

                Screen.TEAMS -> TeamsScreen(viewModel = viewModel)

                Screen.WALLET -> WalletScreen(viewModel = viewModel)

                Screen.HOST_HUB -> HostHubScreen(viewModel = viewModel)

                Screen.ADMIN_CONSOLE -> AdminConsoleScreen(viewModel = viewModel)

                Screen.NOTIFICATIONS -> NotificationsScreen(
                    viewModel = viewModel,
                    onNavigateDeepLink = { link ->
                        currentScreen = Screen.HOME
                    },
                    onNavigateToPdfDoc = {
                        currentScreen = Screen.PDF_DOC
                    }
                )

                Screen.SUPPORT -> SupportScreen(viewModel = viewModel)

                Screen.PROFILE -> ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToHostHub = { currentScreen = Screen.HOST_HUB },
                    onNavigateToSupport = { currentScreen = Screen.SUPPORT },
                    onNavigateToWallet = { currentScreen = Screen.WALLET },
                    onNavigateToPdfDoc = { currentScreen = Screen.PDF_DOC }
                )

                Screen.PDF_DOC -> PdfDocScreen(
                    onBack = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}

@Composable
private fun NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) NeonGreen else TextMuted,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isSelected) NeonGreen else TextMuted,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
        )
    }
}
