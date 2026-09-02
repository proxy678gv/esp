package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

data class ProfileOption(
    val icon: ImageVector,
    val title: String,
    val action: () -> Unit
)

@Composable
fun ProfileScreen(
    viewModel: PGEsportsViewModel,
    onNavigateToHostHub: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToPdfDoc: () -> Unit = {}
) {
    val user by viewModel.currentUser.collectAsState()
    val language by viewModel.language.collectAsState()
    val dbStats by viewModel.databaseStats.collectAsState()

    var showRulebookDialog by remember { mutableStateOf(false) }
    var showGoogleAccountDialog by remember { mutableStateOf(false) }
    var showFreeFireAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchDatabaseStats()
    }

    val settingsOptions = remember {
        listOf(
            ProfileOption(Icons.Default.Whatshot, "Free Fire Player Account & UID") { showFreeFireAccountDialog = true },
            ProfileOption(Icons.Default.PictureAsPdf, "PG E-SPORTS 2.0 Spec PDF", onNavigateToPdfDoc),
            ProfileOption(Icons.Default.AccountBalanceWallet, "Wallet & Payouts", onNavigateToWallet),
            ProfileOption(Icons.Default.CloudSync, "Google ID & Database Sync") { showGoogleAccountDialog = true },
            ProfileOption(Icons.Default.SportsEsports, "Host Hub Operations", onNavigateToHostHub),
            ProfileOption(Icons.Default.MenuBook, "Official Rulebook & Terms") { showRulebookDialog = true },
            ProfileOption(Icons.Default.SupportAgent, "Help Desk & Dispute Support", onNavigateToSupport)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Player Profile Card
        item {
            user?.let { u ->
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen,
                    backgroundColor = DarkCardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = u.username,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = TextPrimary,
                                    fontFamily = SFProFontFamily
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                PGRankBadge(tier = u.rankTier)
                            }
                            Text(
                                text = "IGN: ${u.freeFireIgn} • ${u.fullName}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = SFProFontFamily
                            )
                            Text(
                                text = "PG ID: #${u.id.takeLast(8)} • FF UID: ${u.freeFireUid}",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = DarkCardBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileStatItem("Rating", "${u.rating} PTS", NeonGreen)
                        ProfileStatItem("Matches", "${u.totalMatches}", TextPrimary)
                        ProfileStatItem("Victories", "${u.totalWins}", CyberGold)
                        ProfileStatItem("K/D", "${u.kdRatio}", ElectricBlue)
                    }
                }
            }
        }

        // Free Fire Player Account Details Card (UID, IGN, Level, Rank, Role, Server)
        item {
            FreeFireAccountCard(
                user = user,
                onEditDetails = { showFreeFireAccountDialog = true }
            )
        }

        // Google Account & Database Info Card
        item {
            GoogleAccountCard(
                user = user,
                onSyncDatabase = { viewModel.syncDatabaseWithCloud() },
                onManageAccount = { showGoogleAccountDialog = true }
            )
        }

        // Language & Localization Switcher
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = ElectricBlue)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Language / மொழி", fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = SFProFontFamily)
                            Text(
                                text = if (language == AppLanguage.ENGLISH) "English (Active)" else "தமிழ் (இயக்கத்தில் உள்ளது)",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }

                    Switch(
                        checked = language == AppLanguage.TAMIL,
                        onCheckedChange = { viewModel.toggleLanguage() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonGreen,
                            checkedTrackColor = NeonGreen.copy(alpha = 0.3f),
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = DarkCardBorder
                        )
                    )
                }
            }
        }

        // Navigation Quick Action List
        item {
            Text("PREFERENCES & POLICIES", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextMuted, letterSpacing = 1.sp, fontFamily = SFProFontFamily)
        }

        items(settingsOptions) { option: ProfileOption ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { option.action() },
                shape = RoundedCornerShape(12.dp),
                color = DarkCardBg,
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
                        Icon(imageVector = option.icon, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = option.title, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp, fontFamily = SFProFontFamily)
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }
        }
    }

    // Google Account Manager & Database Dialog
    if (showGoogleAccountDialog) {
        GoogleAccountManagerDialog(
            user = user,
            databaseStats = dbStats,
            onDismiss = { showGoogleAccountDialog = false },
            onSaveGoogleAccount = { googleId, email, name ->
                viewModel.linkGoogleAccount(googleId, email, name)
            },
            onUnlinkGoogle = {
                viewModel.unlinkGoogleAccount()
            },
            onSyncNow = {
                viewModel.syncDatabaseWithCloud()
            }
        )
    }

    // Free Fire Account Manager Dialog
    if (showFreeFireAccountDialog) {
        EditFreeFireAccountDialog(
            user = user,
            onDismiss = { showFreeFireAccountDialog = false },
            onSaveDetails = { uid, ign, level, rank, region, role, guild ->
                viewModel.updateFreeFireAccount(uid, ign, level, rank, region, role, guild)
            }
        )
    }

    if (showRulebookDialog) {
        AlertDialog(
            onDismissRequest = { showRulebookDialog = false },
            containerColor = DarkSurface,
            title = { Text("PG E-SPORTS 2.0 Rulebook 📜", fontWeight = FontWeight.Black, color = TextPrimary, fontFamily = SFProFontFamily) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1. Zero-Tolerance Cheating Policy: All emulator hacks, script modifiers, or aimbots result in permanent account bans & forfeiture of prize pools.", fontSize = 12.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                    Text("2. Authoritative Server Scoring: Leaderboard placement points and kill points are computed strictly server-side.", fontSize = 12.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                    Text("3. Match Check-In: Captains must check-in 15 mins prior to match release. Failure to check-in releases the slot to waitlisted squads.", fontSize = 12.sp, color = TextSecondary)
                    Text("4. Payout Assurance: Verified tournament winnings are credited directly to your PG Wallet and withdrawable to any Indian UPI VPA / Bank account.", fontSize = 12.sp, color = TextSecondary)
                }
            },
            confirmButton = {
                TextButton(onClick = { showRulebookDialog = false }) {
                    Text("Understood", color = NeonGreen, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                }
            }
        )
    }
}

@Composable
private fun ProfileStatItem(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 15.sp, fontFamily = SFProFontFamily)
    }
}
