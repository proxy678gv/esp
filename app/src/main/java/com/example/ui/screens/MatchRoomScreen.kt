package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TournamentEntity
import com.example.data.model.TournamentResultEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchRoomScreen(
    tournament: TournamentEntity,
    viewModel: PGEsportsViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val matchRoom by viewModel.getMatchRoom(tournament.id).collectAsState(initial = null)
    val results by viewModel.getTournamentResults(tournament.id).collectAsState(initial = emptyList())
    val userRegistrations by viewModel.userRegistrations.collectAsState()
    val user by viewModel.currentUser.collectAsState()

    val myReg = remember(userRegistrations, tournament) {
        userRegistrations.find { it.tournamentId == tournament.id }
    }

    var showResultSubmitDialog by remember { mutableStateOf(false) }
    var placementInput by remember { mutableStateOf("1") }
    var killsInput by remember { mutableStateOf("6") }
    var totalScoreInput by remember { mutableStateOf("27") }

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
                    Text("MATCH ROOM & RESULTS", fontSize = 12.sp, color = NeonPurpleLight, fontWeight = FontWeight.Bold)
                    Text(tournament.title, fontSize = 15.sp, fontWeight = FontWeight.Black, color = TextPrimary, maxLines = 1)
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
            // Check-In Status Card
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (myReg?.isCheckedIn == true) NeonGreen else CyberOrange,
                    backgroundColor = DarkCardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (myReg?.isCheckedIn == true) NeonGreen.copy(alpha = 0.2f) else CyberOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (myReg?.isCheckedIn == true) Icons.Default.CheckCircle else Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = if (myReg?.isCheckedIn == true) NeonGreen else CyberOrange
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (myReg?.isCheckedIn == true) "Attendance Verified ✅" else "Check-In Window Live ⏱️",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Squad: ${myReg?.teamName ?: "My Squad"}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        if (myReg != null && !myReg.isCheckedIn) {
                            PGNeonButton(
                                text = "Check In",
                                onClick = { viewModel.checkInTeam(myReg) },
                                containerColor = CyberOrange,
                                glowColor = CyberOrange,
                                contentColor = DarkBackground
                            )
                        }
                    }
                }
            }

            // Player Free Fire UID Card
            item {
                user?.let { u ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF140D0B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.4f))
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
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFF5722).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("PLAYER FREE FIRE UID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                    Text("${u.freeFireUid} • ${u.freeFireIgn}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Surface(
                                color = Color(0xFFFF5722).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Slot #${myReg?.slotNumber ?: 1}",
                                    color = Color(0xFFFFB300),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Room Credentials Box (Host Released or Locked)
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (matchRoom?.isReleased == true) NeonGreen else DarkCardBorder,
                    backgroundColor = DarkSurface
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "CUSTOM ROOM CREDENTIALS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (matchRoom?.isReleased == true) NeonGreen else TextMuted,
                            letterSpacing = 1.sp
                        )

                        if (matchRoom?.isReleased == true) {
                            Surface(color = NeonGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "RELEASED 🔓",
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Surface(color = CyberRed.copy(alpha = 0.2f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = "LOCKED 🔒",
                                    color = CyberRed,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (matchRoom?.isReleased == true) {
                        // Room ID Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("ROOM ID", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(matchRoom?.roomId ?: "", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Room ID", matchRoom?.roomId ?: ""))
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Room ID", tint = NeonGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Room Password Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("PASSWORD", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Text(matchRoom?.roomPassword ?: "", fontSize = 18.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Room Password", matchRoom?.roomPassword ?: ""))
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy Password", tint = NeonGreen)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "💡 Paste Room ID & Password into the game custom lobby. Do not share credentials with non-registered players.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Room credentials will auto-reveal 10 minutes before match start.",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Results & Proof Submission Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "MATCH STANDINGS & EVIDENCE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )

                    PGSecondaryButton(
                        text = "Submit Proof 📸",
                        onClick = { showResultSubmitDialog = true }
                    )
                }
            }

            if (results.isEmpty()) {
                item {
                    Text(
                        text = "No results recorded yet for this match.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            } else {
                items(results) { res: TournamentResultEntity ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = if (res.placement == 1) CyberGold.copy(alpha = 0.2f) else DarkBackground,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "#${res.placement}",
                                        color = if (res.placement == 1) CyberGold else TextPrimary,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(res.teamName, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Text("${res.kills} Kills • ${res.placementPoints} Place Pts", color = TextSecondary, fontSize = 12.sp)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${res.totalScore} PTS",
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = res.status,
                                    color = if (res.status == "VERIFIED") NeonGreen else CyberGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // --- Submit Result Modal ---
    if (showResultSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showResultSubmitDialog = false },
            containerColor = DarkSurface,
            title = { Text("Submit Match Result & Evidence", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Team Name: ${myReg?.teamName ?: "My Squad"}", fontWeight = FontWeight.Bold, color = NeonGreen)

                    OutlinedTextField(
                        value = placementInput,
                        onValueChange = { placementInput = it },
                        label = { Text("Final Placement (e.g. 1)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = killsInput,
                        onValueChange = { killsInput = it },
                        label = { Text("Total Squad Kills (e.g. 8)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = totalScoreInput,
                        onValueChange = { totalScoreInput = it },
                        label = { Text("Calculated Total Score") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = DarkSurfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null, tint = NeonGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Screenshot attached: ff_match_victory.png", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Submit for Admin Review",
                    onClick = {
                        viewModel.submitMatchResult(
                            tournamentId = tournament.id,
                            teamName = myReg?.teamName ?: "Thunder Squad",
                            placement = placementInput.toIntOrNull() ?: 1,
                            kills = killsInput.toIntOrNull() ?: 0,
                            score = totalScoreInput.toIntOrNull() ?: 0,
                            proofUrl = "evidence/ff_match_proof.png"
                        )
                        showResultSubmitDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showResultSubmitDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
