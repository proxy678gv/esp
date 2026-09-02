package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.notification.MatchNotificationHelper
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@Composable
fun TournamentDetailScreen(
    tournament: TournamentEntity,
    viewModel: PGEsportsViewModel,
    onBack: () -> Unit,
    onNavigateToMatchRoom: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val userTeams by viewModel.userTeams.collectAsState()
    val registrations by viewModel.getTournamentRegistrations(tournament.id).collectAsState(initial = emptyList())

    var showRegistrationDialog by remember { mutableStateOf(false) }
    var showRazorpayTournamentCheckout by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<TeamEntity?>(userTeams.firstOrNull()) }
    var acceptedRules by remember { mutableStateOf(false) }
    var isRegistering by remember { mutableStateOf(false) }

    val isUserRegistered = remember(registrations, user) {
        registrations.any { it.captainId == user?.id }
    }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkBackground)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = tournament.gameName,
                        fontSize = 12.sp,
                        color = NeonGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tournament.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        maxLines = 1
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("ENTRY FEE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (tournament.entryFee == 0.0) "FREE" else "₹${tournament.entryFee.toInt()}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen
                        )
                    }

                    if (isUserRegistered) {
                        PGNeonButton(
                            text = "Enter Match Room 🔑",
                            onClick = onNavigateToMatchRoom,
                            containerColor = NeonPurple,
                            glowColor = NeonPurple,
                            contentColor = TextPrimary
                        )
                    } else if (tournament.status == TournamentStatus.REGISTRATION_OPEN) {
                        PGNeonButton(
                            text = "Register Team ⚔️",
                            onClick = { showRegistrationDialog = true },
                            enabled = tournament.registeredTeamsCount < tournament.maxTeams
                        )
                    } else {
                        Text(
                            text = "Registration Closed",
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status & Title Card
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen,
                    backgroundColor = DarkCardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PGStatusBadge(status = tournament.status)
                        Text(
                            text = "Hosted by ${tournament.hostName}",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = tournament.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tournament.description,
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            // 15-Minute Local Match Alarm Reminder Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isUserRegistered) NeonGreen.copy(alpha = 0.1f) else DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isUserRegistered) NeonGreen else DarkCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isUserRegistered) Icons.Default.AlarmOn else Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = if (isUserRegistered) NeonGreen else ElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isUserRegistered) "15-Min Match Alarm: ACTIVE ⏰" else "15-Min Match Local Reminder",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isUserRegistered) NeonGreen else TextPrimary,
                                    fontFamily = SFProFontFamily
                                )
                                Text(
                                    text = if (isUserRegistered)
                                        "Alarm is set for 15 mins before match start. Custom Room ID & Pass will be delivered."
                                    else
                                        "Registers automatic high-priority local push notification 15m prior to start.",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }

                        // Instant Test Alarm Button
                        Button(
                            onClick = {
                                MatchNotificationHelper.show15MinReminderNotification(
                                    context = context,
                                    tournamentId = tournament.id,
                                    tournamentTitle = tournament.title,
                                    roomId = "9823412",
                                    roomPassword = "PG99",
                                    slotNumber = 12
                                )
                                Toast.makeText(context, "15-Minute Reminder Alarm Triggered! 🚨", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isUserRegistered) NeonGreen.copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.2f),
                                contentColor = if (isUserRegistered) NeonGreen else ElectricBlue
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isUserRegistered) NeonGreen else ElectricBlue)
                        ) {
                            Icon(Icons.Default.Vibration, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Alarm", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                        }
                    }
                }
            }

            // Prize Distribution Card
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = CyberGold,
                    backgroundColor = DarkSurface
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "🏆 PRIZE POOL BREAKDOWN",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Total ₹${tournament.prizePool.toInt()}",
                            fontWeight = FontWeight.Black,
                            color = CyberGold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1st Prize
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = CyberGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🥇 1st Place", fontSize = 11.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                                Text("₹${tournament.prizeFirst.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }

                        // 2nd Prize
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🥈 2nd Place", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("₹${tournament.prizeSecond.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }

                        // 3rd Prize
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🥉 3rd Place", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("₹${tournament.prizeThird.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                        }
                    }
                }
            }

            // Tournament Parameters & Format
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkCardBg
                ) {
                    Text(
                        text = "⚙️ MATCH SPECIFICATIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val specs = listOf(
                        "Mode & Format" to "${tournament.mode.name} (${tournament.format.name})",
                        "Map" to tournament.mapName,
                        "Server Region" to tournament.serverRegion,
                        "Kill Scoring" to "${tournament.killPoints} Pts per Kill",
                        "Placement 1st" to "${tournament.firstPlacementPoints} Pts"
                    )

                    specs.forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = label, color = TextSecondary, fontSize = 13.sp)
                            Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Rules
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurface
                ) {
                    Text(
                        text = "📜 OFFICIAL RULES & GUIDELINES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = CyberOrange,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tournament.rules,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            // Registered Teams List
            item {
                Text(
                    text = "REGISTERED SQUADS (${registrations.size}/${tournament.maxTeams})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            if (registrations.isEmpty()) {
                item {
                    Text(
                        text = "No squads registered yet. Be the first to claim slot #1!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                items(registrations) { reg ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = NeonGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "#${reg.slotNumber}",
                                        color = NeonGreen,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = reg.teamName,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Captain: ${reg.captainName}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (reg.isCheckedIn) {
                                Surface(
                                    color = NeonGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Checked-in ✅",
                                        color = NeonGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Registration & Atomic Slot Lock Modal ---
    if (showRegistrationDialog) {
        AlertDialog(
            onDismissRequest = { if (!isRegistering) showRegistrationDialog = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Tournament Registration ⚔️",
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Verified Player Free Fire UID Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFF1F110B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Free Fire Player UID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, fontFamily = SFProFontFamily)
                                    Text("${user?.freeFireUid ?: "1928374650"} (${user?.freeFireIgn ?: user?.inGameId ?: "IGN"})", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, fontFamily = SFProFontFamily)
                                }
                            }
                            Surface(
                                color = Color(0xFFFF5722).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = user?.freeFireServerRegion ?: "IND Server",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFB300),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }
                    }

                    Text(
                        text = "1. Select Registered Squad",
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        fontSize = 13.sp
                    )

                    if (userTeams.isEmpty()) {
                        Text(
                            text = "You need to create a squad first under the Teams tab!",
                            color = CyberRed,
                            fontSize = 12.sp
                        )
                    } else {
                        userTeams.forEach { team ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedTeam = team },
                                shape = RoundedCornerShape(10.dp),
                                color = if (selectedTeam?.id == team.id) NeonPurple.copy(alpha = 0.25f) else DarkCardBg,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedTeam?.id == team.id) NeonPurple else DarkCardBorder
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedTeam?.id == team.id,
                                        onClick = { selectedTeam = team },
                                        colors = RadioButtonDefaults.colors(selectedColor = NeonGreen)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(team.name, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("Roster: ${team.memberNames}", fontSize = 11.sp, color = TextSecondary, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }

                    Divider(color = DarkCardBorder)

                    // Wallet & Payment Summary
                    Column {
                        Text("2. Payment & Verification", fontWeight = FontWeight.Bold, color = NeonGreen, fontSize = 13.sp, fontFamily = SFProFontFamily)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Entry Fee:", color = TextSecondary, fontSize = 13.sp, fontFamily = SFProFontFamily)
                            Text("₹${tournament.entryFee.toInt()}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = SFProFontFamily)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("PG Wallet Balance:", color = TextSecondary, fontSize = 13.sp, fontFamily = SFProFontFamily)
                            Text("₹${user?.walletBalance?.toInt() ?: 0}", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp, fontFamily = SFProFontFamily)
                        }

                        val hasInsufficientBalance = tournament.entryFee > (user?.walletBalance ?: 0.0)
                        if (hasInsufficientBalance && tournament.entryFee > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = Color(0xFF0C2340),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3395FF).copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color(0xFF3395FF), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Razorpay Fast Checkout will be opened to pay entry fee directly via UPI/Cards.", fontSize = 10.sp, color = Color.White.copy(alpha = 0.9f), fontFamily = SFProFontFamily)
                                }
                            }
                        }
                    }

                    // Fair Play Acceptance Checkbox
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { acceptedRules = !acceptedRules }
                    ) {
                        Checkbox(
                            checked = acceptedRules,
                            onCheckedChange = { acceptedRules = it },
                            colors = CheckboxDefaults.colors(checkedColor = NeonGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I accept fair play rules, no-hack policy, and check-in timeline.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = SFProFontFamily
                        )
                    }
                }
            },
            confirmButton = {
                val hasInsufficientBalance = tournament.entryFee > (user?.walletBalance ?: 0.0)
                PGNeonButton(
                    text = if (hasInsufficientBalance && tournament.entryFee > 0) "Pay via Razorpay 💳" else "Pay & Confirm Slot",
                    enabled = acceptedRules && selectedTeam != null && !isRegistering,
                    onClick = {
                        if (hasInsufficientBalance && tournament.entryFee > 0) {
                            showRazorpayTournamentCheckout = true
                        } else {
                            isRegistering = true
                            selectedTeam?.let { team ->
                                viewModel.registerTournament(tournament, team) {
                                    isRegistering = false
                                    showRegistrationDialog = false
                                    MatchNotificationHelper.schedule15MinReminder(
                                        context = context,
                                        tournamentId = tournament.id,
                                        tournamentTitle = tournament.title,
                                        matchStartTimeMillis = tournament.startDateTimeMillis,
                                        roomId = "9823412",
                                        roomPassword = "PG99",
                                        slotNumber = 12
                                    )
                                    Toast.makeText(context, "Slot Confirmed! 15-min match alarm scheduled ⏰", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                )
            },
            dismissButton = {
                TextButton(
                    onClick = { showRegistrationDialog = false },
                    enabled = !isRegistering
                ) {
                    Text("Cancel", color = TextSecondary, fontFamily = SFProFontFamily)
                }
            }
        )
    }

    // Razorpay Checkout Modal for Tournament Entry
    if (showRazorpayTournamentCheckout) {
        selectedTeam?.let { team ->
            RazorpayCheckoutDialog(
                amount = tournament.entryFee,
                description = "Entry Fee: ${tournament.title}",
                customerEmail = user?.email ?: "player@pgesports.com",
                customerPhone = user?.mobile ?: "+91 98765 43210",
                tournament = tournament,
                team = team,
                user = user,
                type = com.example.payment.PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
                onDismiss = { showRazorpayTournamentCheckout = false },
                onPaymentSuccess = { result ->
                    viewModel.processRazorpayTournamentEntry(
                        tournament = tournament,
                        team = team,
                        paymentResult = result
                    ) {
                        showRazorpayTournamentCheckout = false
                        showRegistrationDialog = false
                        MatchNotificationHelper.schedule15MinReminder(
                            context = context,
                            tournamentId = tournament.id,
                            tournamentTitle = tournament.title,
                            matchStartTimeMillis = tournament.startDateTimeMillis,
                            roomId = "9823412",
                            roomPassword = "PG99",
                            slotNumber = tournament.registeredTeamsCount + 1
                        )
                        Toast.makeText(context, "Razorpay Payment Verified! Slot Confirmed & 15m alarm scheduled ⏰", Toast.LENGTH_LONG).show()
                    }
                },
                onPaymentError = { errorResult ->
                    viewModel.recordFailedRazorpayPayment(
                        tournamentId = tournament.id,
                        tournamentTitle = tournament.title,
                        teamName = team.name,
                        amount = tournament.entryFee,
                        errorCode = errorResult.errorCode ?: -1,
                        errorMessage = errorResult.errorMessage ?: "Payment declined"
                    )
                }
            )
        }
    }
}
