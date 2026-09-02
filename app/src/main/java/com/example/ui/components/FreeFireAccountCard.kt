package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.UserEntity
import com.example.ui.theme.*

// Free Fire Brand Theme Colors
val FreeFireOrange = Color(0xFFFF5722)
val FreeFireGold = Color(0xFFFFB300)
val FreeFireRed = Color(0xFFD32F2F)
val FreeFireDarkBg = Color(0xFF140D0B)

@Composable
fun FreeFireAccountCard(
    user: UserEntity?,
    onEditDetails: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val ffUid = user?.freeFireUid?.takeIf { it.isNotBlank() } ?: "1928374650"
    val ffIgn = user?.freeFireIgn?.takeIf { it.isNotBlank() } ?: user?.inGameId ?: "PG_THUNDER_99"
    val ffLevel = user?.freeFireLevel ?: 72
    val ffRank = user?.freeFireRankTier?.takeIf { it.isNotBlank() } ?: "Grandmaster"
    val ffRegion = user?.freeFireServerRegion?.takeIf { it.isNotBlank() } ?: "IND (India)"
    val ffRole = user?.freeFireBattleRole?.takeIf { it.isNotBlank() } ?: "Rusher / Assaulter"
    val ffGuild = user?.freeFireGuildName?.takeIf { it.isNotBlank() } ?: "PG ESPORTS ELITE"
    val ffLikes = user?.freeFireLikes ?: 14250
    val ffHeadshot = user?.freeFireHeadshotRate ?: 48.5f

    PGGlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = FreeFireOrange,
        backgroundColor = DarkCardBg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header with Fire Badge & Garena Label
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
                            .background(
                                Brush.linearGradient(
                                    listOf(FreeFireRed, FreeFireOrange, FreeFireGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Free Fire",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Free Fire Player ID",
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = TextPrimary,
                                fontFamily = SFProFontFamily
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = FreeFireOrange.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(0.5.dp, FreeFireOrange)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = null,
                                        tint = FreeFireOrange,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "VERIFIED",
                                        color = FreeFireOrange,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SFProFontFamily
                                    )
                                }
                            }
                        }
                        Text(
                            text = "Official Garena In-Game Identity",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = SFProFontFamily
                        )
                    }
                }

                IconButton(
                    onClick = onEditDetails,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Free Fire ID",
                        tint = FreeFireGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Primary Highlight Box: Player UID & IGN
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = FreeFireDarkBg,
                border = BorderStroke(1.dp, FreeFireOrange.copy(alpha = 0.35f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Free Fire UID Row with 1-Tap Copy
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PLAYER UID",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 1.sp,
                                fontFamily = SFProFontFamily
                            )
                            Text(
                                text = ffUid,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.5.sp,
                                fontFamily = SFProFontFamily
                            )
                        }

                        // Copy Button
                        FilledTonalButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Free Fire UID", ffUid)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Free Fire UID ($ffUid) copied to clipboard! 📋", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = FreeFireOrange.copy(alpha = 0.25f),
                                contentColor = FreeFireGold
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy UID", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                        }
                    }

                    HorizontalDivider(color = FreeFireOrange.copy(alpha = 0.2f))

                    // IGN & Guild Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "IN-GAME NAME (IGN)",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                fontFamily = SFProFontFamily
                            )
                            Text(
                                text = "⚡ $ffIgn",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonGreen,
                                fontFamily = SFProFontFamily
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "GUILD SQUAD",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                fontFamily = SFProFontFamily
                            )
                            Text(
                                text = "🛡️ $ffGuild",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }
                }
            }

            // Stats Matrix: Level, Rank, Region, Role
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FFStatChip(
                    title = "LEVEL",
                    value = "Lvl $ffLevel",
                    color = FreeFireGold,
                    modifier = Modifier.weight(1f)
                )
                FFStatChip(
                    title = "RANK TIER",
                    value = ffRank,
                    color = CyberRed,
                    modifier = Modifier.weight(1.3f)
                )
                FFStatChip(
                    title = "SERVER",
                    value = ffRegion,
                    color = ElectricBlue,
                    modifier = Modifier.weight(1.2f)
                )
            }

            // Secondary Info Bar: Battle Role & Headshot Rate
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = FreeFireGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Role: $ffRole",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        fontFamily = SFProFontFamily
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ThumbUp, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$ffLikes Likes • HS: $ffHeadshot%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = SFProFontFamily
                    )
                }
            }
        }
    }
}

@Composable
private fun FFStatChip(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = DarkSurfaceVariant,
        border = BorderStroke(0.5.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                fontFamily = SFProFontFamily
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = color,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontFamily = SFProFontFamily
            )
        }
    }
}

/**
 * Interactive Dialog for updating player's Free Fire Account Details
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFreeFireAccountDialog(
    user: UserEntity?,
    onDismiss: () -> Unit,
    onSaveDetails: (uid: String, ign: String, level: Int, rankTier: String, serverRegion: String, battleRole: String, guildName: String) -> Unit
) {
    var uid by remember { mutableStateOf(user?.freeFireUid ?: "1928374650") }
    var ign by remember { mutableStateOf(user?.freeFireIgn ?: user?.inGameId ?: "PG_THUNDER_99") }
    var levelText by remember { mutableStateOf((user?.freeFireLevel ?: 72).toString()) }
    var selectedRank by remember { mutableStateOf(user?.freeFireRankTier ?: "Grandmaster") }
    var selectedRegion by remember { mutableStateOf(user?.freeFireServerRegion ?: "IND (India)") }
    var selectedRole by remember { mutableStateOf(user?.freeFireBattleRole ?: "Rusher / Assaulter") }
    var guildName by remember { mutableStateOf(user?.freeFireGuildName ?: "PG ESPORTS ELITE") }

    val rankTiers = listOf("Grandmaster", "Master", "Heroic", "Diamond", "Platinum", "Gold")
    val regions = listOf("IND (India)", "BD (Bangladesh)", "PK (Pakistan)", "SG (Singapore)", "MENA", "Global")
    val battleRoles = listOf("Rusher / Assaulter", "Sniper Specialist", "Support / Medic", "In-Game Leader (IGL)")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = DarkSurface,
            border = BorderStroke(1.dp, FreeFireOrange.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dialog Header
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
                                .background(FreeFireOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Whatshot, contentDescription = null, tint = FreeFireOrange, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Free Fire Account Details",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                fontFamily = SFProFontFamily
                            )
                            Text(
                                text = "Verify UID for tournament rooms & slot check-ins",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                HorizontalDivider(color = DarkCardBorder)

                // Free Fire UID Field
                OutlinedTextField(
                    value = uid,
                    onValueChange = { uid = it.filter { char -> char.isDigit() }.take(12) },
                    label = { Text("Free Fire Player UID (8-12 Digits)", fontFamily = SFProFontFamily) },
                    placeholder = { Text("e.g. 1928374650", fontFamily = SFProFontFamily) },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = FreeFireOrange) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = FreeFireOrange,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedLabelColor = FreeFireOrange
                    )
                )

                // Free Fire IGN Field
                OutlinedTextField(
                    value = ign,
                    onValueChange = { ign = it.take(20) },
                    label = { Text("In-Game Nickname (IGN)", fontFamily = SFProFontFamily) },
                    placeholder = { Text("e.g. ⚡ THUNDER_OP ⚡", fontFamily = SFProFontFamily) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = NeonGreen) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGreen,
                        unfocusedBorderColor = DarkCardBorder,
                        focusedLabelColor = NeonGreen
                    )
                )

                // Level & Guild Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = levelText,
                        onValueChange = { levelText = it.filter { c -> c.isDigit() }.take(3) },
                        label = { Text("Account Level", fontFamily = SFProFontFamily) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = FreeFireGold,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )

                    OutlinedTextField(
                        value = guildName,
                        onValueChange = { guildName = it.take(24) },
                        label = { Text("Guild Name", fontFamily = SFProFontFamily) },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkCardBorder
                        )
                    )
                }

                // Rank Tier Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("CURRENT RANK TIER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, fontFamily = SFProFontFamily)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rankTiers.forEach { rank ->
                            val isSelected = selectedRank == rank
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRank = rank },
                                label = { Text(rank, fontSize = 12.sp, fontFamily = SFProFontFamily) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FreeFireOrange.copy(alpha = 0.25f),
                                    selectedLabelColor = FreeFireGold
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) FreeFireOrange else DarkCardBorder
                                )
                            )
                        }
                    }
                }

                // Server / Region Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("SERVER / REGION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, fontFamily = SFProFontFamily)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        regions.forEach { region ->
                            val isSelected = selectedRegion == region
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRegion = region },
                                label = { Text(region, fontSize = 12.sp, fontFamily = SFProFontFamily) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                                    selectedLabelColor = ElectricBlue
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if (isSelected) ElectricBlue else DarkCardBorder
                                )
                            )
                        }
                    }
                }

                // Battle Role Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("PREFERRED BATTLE ROLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, fontFamily = SFProFontFamily)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        battleRoles.forEach { role ->
                            val isSelected = selectedRole == role
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRole = role },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) DarkSurfaceVariant else DarkCardBg,
                                border = BorderStroke(1.dp, if (isSelected) FreeFireOrange else DarkCardBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = role,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontFamily = SFProFontFamily
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = FreeFireOrange, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Save CTA Button
                Button(
                    onClick = {
                        val parsedLevel = levelText.toIntOrNull() ?: 72
                        onSaveDetails(
                            uid.ifBlank { "1928374650" },
                            ign.ifBlank { "PG_THUNDER_99" },
                            parsedLevel,
                            selectedRank,
                            selectedRegion,
                            selectedRole,
                            guildName.ifBlank { "PG ESPORTS ELITE" }
                        )
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FreeFireOrange,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verify & Save Free Fire ID",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        fontFamily = SFProFontFamily
                    )
                }
            }
        }
    }
}
