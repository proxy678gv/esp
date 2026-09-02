package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@Composable
fun HostHubScreen(
    viewModel: PGEsportsViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val allTournaments by viewModel.allTournaments.collectAsState()
    val hostTournaments = remember(allTournaments, user) {
        allTournaments.filter { it.hostId == user?.id || it.hostName == user?.username }
    }

    var showCreateTournamentDialog by remember { mutableStateOf(false) }
    var showReleaseRoomDialog by remember { mutableStateOf<TournamentEntity?>(null) }
    var showApplyHostDialog by remember { mutableStateOf(false) }

    // Host Application Form States
    var fullNameInput by remember { mutableStateOf(user?.fullName ?: "") }
    var emailInput by remember { mutableStateOf(user?.email ?: "") }
    var mobileInput by remember { mutableStateOf(user?.mobile ?: "") }
    var cityInput by remember { mutableStateOf("Madurai, Tamil Nadu") }
    var gamingExpInput by remember { mutableStateOf("3 years competitive Free Fire & BGMI organizer") }
    var reasonInput by remember { mutableStateOf("Hosting regular weekend cups for Tamil community") }

    // Tournament Creation Form States
    var tourTitleInput by remember { mutableStateOf("") }
    var tourGameInput by remember { mutableStateOf("Free Fire") }
    var tourModeInput by remember { mutableStateOf(TournamentMode.CLASH_SQUAD) }
    var tourFormatInput by remember { mutableStateOf(TournamentFormat.BEST_OF_3) }
    var tourMapInput by remember { mutableStateOf("Bermuda Clash") }
    var tourEntryFeeInput by remember { mutableStateOf("50") }
    var tourPrizePoolInput by remember { mutableStateOf("3000") }
    var tourMaxTeamsInput by remember { mutableStateOf("16") }
    var tourRulesInput by remember { mutableStateOf("1. Gun Property OFF 2. Character Skill ON 3. No hacks allowed.") }

    // Release Room States
    var releaseRoomIdInput by remember { mutableStateOf("9182390") }
    var releaseRoomPassInput by remember { mutableStateOf("PG_LIVE_99") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Host Status Header Card
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonPurple,
                backgroundColor = DarkCardBg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("HOST OPERATIONS HUB", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("Tournament Management Desk", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text(
                            text = if (user?.isHostApproved == true) "✅ Verified Official Host Account" else "⚠️ Host privileges pending Super Admin verification",
                            color = if (user?.isHostApproved == true) NeonGreen else CyberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (user?.isHostApproved != true) {
                        PGNeonButton(
                            text = "Apply as Host 📋",
                            onClick = { showApplyHostDialog = true }
                        )
                    }
                }

                if (user?.isHostApproved == true) {
                    Spacer(modifier = Modifier.height(14.dp))
                    PGNeonButton(
                        text = "+ Host New Tournament",
                        onClick = { showCreateTournamentDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Add
                    )
                }
            }
        }

        // Host's Created Tournaments
        item {
            Text(
                text = "MY HOSTED TOURNAMENTS (${hostTournaments.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        if (hostTournaments.isEmpty()) {
            item {
                PGGlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = DarkSurface) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No tournaments hosted yet.", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Click '+ Host New Tournament' to configure game rules, prizes, and slots.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(hostTournaments) { tour ->
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = DarkCardBorder,
                    backgroundColor = DarkCardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PGStatusBadge(status = tour.status)
                        Text("${tour.registeredTeamsCount}/${tour.maxTeams} Teams Joined", color = TextSecondary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(tour.title, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                    Text("Game: ${tour.gameName} • Map: ${tour.mapName} • Prize: ₹${tour.prizePool.toInt()}", color = TextSecondary, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PGSecondaryButton(
                            text = "Release Room ID 🔑",
                            onClick = { showReleaseRoomDialog = tour },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    // Apply as Host Dialog
    if (showApplyHostDialog) {
        AlertDialog(
            onDismissRequest = { showApplyHostDialog = false },
            containerColor = DarkSurface,
            title = { Text("Apply for Host Privileges 📋", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fullNameInput, onValueChange = { fullNameInput = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = cityInput, onValueChange = { cityInput = it }, label = { Text("City & State") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = gamingExpInput, onValueChange = { gamingExpInput = it }, label = { Text("Esports/Gaming Background") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = reasonInput, onValueChange = { reasonInput = it }, label = { Text("Reason for Hosting") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Submit Application",
                    onClick = {
                        viewModel.applyAsHost(
                            fullName = fullNameInput,
                            email = emailInput,
                            mobile = mobileInput,
                            city = cityInput,
                            gamingExp = gamingExpInput,
                            interestedGames = "Free Fire, BGMI",
                            hostingExp = "College esports lead",
                            reason = reasonInput
                        )
                        showApplyHostDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showApplyHostDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    // Release Room Dialog
    showReleaseRoomDialog?.let { tour ->
        AlertDialog(
            onDismissRequest = { showReleaseRoomDialog = null },
            containerColor = DarkSurface,
            title = { Text("Release Room Credentials 🔑", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Tournament: ${tour.title}", fontSize = 12.sp, color = NeonGreen)
                    OutlinedTextField(value = releaseRoomIdInput, onValueChange = { releaseRoomIdInput = it }, label = { Text("Custom Lobby Room ID") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = releaseRoomPassInput, onValueChange = { releaseRoomPassInput = it }, label = { Text("Lobby Password") }, modifier = Modifier.fillMaxWidth())
                    Text("⚠️ Only checked-in participants will receive room credentials instantly on their app.", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Broadcast to Teams",
                    onClick = {
                        viewModel.releaseRoomCredentials(tour.id, releaseRoomIdInput, releaseRoomPassInput)
                        showReleaseRoomDialog = null
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showReleaseRoomDialog = null }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    // Create Tournament Dialog
    if (showCreateTournamentDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTournamentDialog = false },
            containerColor = DarkSurface,
            title = { Text("Host New Esports Tournament ⚔️", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = tourTitleInput, onValueChange = { tourTitleInput = it }, label = { Text("Tournament Title") }, placeholder = { Text("Free Fire Night Clash Cup") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tourGameInput, onValueChange = { tourGameInput = it }, label = { Text("Game (Free Fire / BGMI / Valorant)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tourMapInput, onValueChange = { tourMapInput = it }, label = { Text("Map Name (e.g. Bermuda Clash)") }, modifier = Modifier.fillMaxWidth())

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = tourEntryFeeInput, onValueChange = { tourEntryFeeInput = it }, label = { Text("Entry Fee (₹)") }, modifier = Modifier.weight(1f))
                        OutlinedTextField(value = tourPrizePoolInput, onValueChange = { tourPrizePoolInput = it }, label = { Text("Prize Pool (₹)") }, modifier = Modifier.weight(1f))
                    }

                    OutlinedTextField(value = tourMaxTeamsInput, onValueChange = { tourMaxTeamsInput = it }, label = { Text("Max Squad Slots (e.g. 12)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = tourRulesInput, onValueChange = { tourRulesInput = it }, label = { Text("Custom Rules") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Publish Tournament",
                    enabled = tourTitleInput.isNotBlank(),
                    onClick = {
                        viewModel.createTournament(
                            title = tourTitleInput.trim(),
                            gameId = if (tourGameInput.contains("BGMI", true)) "BGMI" else "FREE_FIRE",
                            gameName = tourGameInput.trim(),
                            mode = tourModeInput,
                            format = tourFormatInput,
                            mapName = tourMapInput.trim(),
                            entryFee = tourEntryFeeInput.toDoubleOrNull() ?: 0.0,
                            prizePool = tourPrizePoolInput.toDoubleOrNull() ?: 1000.0,
                            maxTeams = tourMaxTeamsInput.toIntOrNull() ?: 12,
                            rules = tourRulesInput
                        )
                        showCreateTournamentDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showCreateTournamentDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}
