package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.UserEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// Google Brand Colors
val GoogleBlue = Color(0xFF4285F4)
val GoogleRed = Color(0xFFEA4335)
val GoogleYellow = Color(0xFFFBBC05)
val GoogleGreen = Color(0xFF34A853)

@Composable
fun GoogleAccountCard(
    user: UserEntity?,
    onSyncDatabase: () -> Unit,
    onManageAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    val isLinked = user?.isGoogleLinked == true
    val googleEmail = user?.googleEmail?.takeIf { it.isNotEmpty() } ?: "proxyf388@gmail.com"
    val googleName = user?.googleDisplayName?.takeIf { it.isNotEmpty() } ?: user?.fullName ?: "Google User"
    val googleId = user?.googleId?.takeIf { it.isNotEmpty() } ?: "1098472910398471029"
    val lastSync = user?.lastCloudSyncTimestamp ?: System.currentTimeMillis()

    PGGlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = if (isLinked) GoogleBlue else DarkCardBorder,
        backgroundColor = DarkCardBg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header: Google Identity Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Google 4-Color Logo Circle
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "G",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = GoogleBlue,
                            fontFamily = SFProFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Google Account ID",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontFamily = SFProFontFamily
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isLinked) {
                                Surface(
                                    color = GoogleGreen.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = BorderStroke(0.5.dp, GoogleGreen)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = GoogleGreen, modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "LINKED",
                                            color = GoogleGreen,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = SFProFontFamily
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Room DB & Cloud Account Sync",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = SFProFontFamily
                        )
                    }
                }

                IconButton(
                    onClick = onManageAccount,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Manage Google Account",
                        tint = ElectricBlue
                    )
                }
            }

            Divider(color = DarkCardBorder)

            // User Info Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with Google ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(GoogleBlue, GoogleRed, GoogleYellow, GoogleGreen, GoogleBlue)),
                            CircleShape
                        )
                        .background(DarkBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = GoogleBlue,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = googleName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = SFProFontFamily
                    )
                    Text(
                        text = googleEmail,
                        fontSize = 12.sp,
                        color = ElectricBlue,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = SFProFontFamily
                    )
                    Text(
                        text = "Google ID: $googleId",
                        fontSize = 10.sp,
                        color = TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontFamily = SFProFontFamily
                    )
                }
            }

            // Sync Status & Database Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = "Last DB Sync: ${dateFormat.format(Date(lastSync))}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontFamily = SFProFontFamily
                        )
                        Text(
                            text = "Play Games ID: ${user?.googlePlayGamesId ?: "PG_GamerTag_9921"}",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontFamily = SFProFontFamily
                        )
                    }
                }

                Button(
                    onClick = onSyncDatabase,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoogleBlue.copy(alpha = 0.2f),
                        contentColor = GoogleBlue
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    border = BorderStroke(1.dp, GoogleBlue.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync DB", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                }
            }
        }
    }
}

@Composable
fun GoogleAccountManagerDialog(
    user: UserEntity?,
    databaseStats: Map<String, Int>,
    onDismiss: () -> Unit,
    onSaveGoogleAccount: (googleId: String, email: String, displayName: String) -> Unit,
    onUnlinkGoogle: () -> Unit,
    onSyncNow: () -> Unit
) {
    var selectedPresetEmail by remember { mutableStateOf(user?.googleEmail ?: "proxyf388@gmail.com") }
    var inputGoogleId by remember { mutableStateOf(user?.googleId ?: "1098472910398471029") }
    var inputDisplayName by remember { mutableStateOf(user?.googleDisplayName ?: "Arun Karthik (Google User)") }
    var isSyncing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val accountPresets = listOf(
        Triple("proxyf388@gmail.com", "Proxy F (Current User)", "1098472910398471029"),
        Triple("arun.esports@gmail.com", "Arun Karthik (Thunder)", "1049284019284918201"),
        Triple("pg.proteam@gmail.com", "PG Pro Esports ID", "1083920194820194820")
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("G", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GoogleBlue, fontFamily = SFProFontFamily)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Google Account & Database",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            fontFamily = SFProFontFamily
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                // Account Selection Presets
                Text(
                    text = "SELECT GOOGLE ID ACCOUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp,
                    fontFamily = SFProFontFamily
                )

                accountPresets.forEach { (email, name, gid) ->
                    val isSelected = selectedPresetEmail == email
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPresetEmail = email
                                inputDisplayName = name
                                inputGoogleId = gid
                            },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) GoogleBlue.copy(alpha = 0.15f) else DarkCardBg,
                        border = BorderStroke(1.dp, if (isSelected) GoogleBlue else DarkCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                                Text(email, fontSize = 12.sp, color = GoogleBlue, fontFamily = SFProFontFamily)
                                Text("ID: $gid", fontSize = 10.sp, color = TextMuted, fontFamily = SFProFontFamily)
                            }
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    selectedPresetEmail = email
                                    inputDisplayName = name
                                    inputGoogleId = gid
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = GoogleBlue)
                            )
                        }
                    }
                }

                // Custom Google Email & ID Input
                OutlinedTextField(
                    value = selectedPresetEmail,
                    onValueChange = { selectedPresetEmail = it },
                    label = { Text("Google Account Email", fontFamily = SFProFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoogleBlue,
                        unfocusedBorderColor = DarkCardBorder
                    )
                )

                OutlinedTextField(
                    value = inputGoogleId,
                    onValueChange = { inputGoogleId = it },
                    label = { Text("Google ID (Subject/OAuth ID)", fontFamily = SFProFontFamily) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoogleBlue,
                        unfocusedBorderColor = DarkCardBorder
                    )
                )

                // Database Record Statistics Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Room Database Metrics", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                            Surface(color = NeonGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                                Text("SQLITE ACTIVE", color = NeonGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp), fontFamily = SFProFontFamily)
                            }
                        }
                        Divider(color = DarkCardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Database Name", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                            Text("pg_esports_db", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = SFProFontFamily)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Cached Tournaments", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                            Text("${databaseStats["Tournaments"] ?: 4} records", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Registered Squads", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                            Text("${databaseStats["Teams"] ?: 3} squads", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Wallet Ledger Transactions", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                            Text("${databaseStats["Transactions"] ?: 8} txns", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        }
                    }
                }

                // Sync Action Button
                Button(
                    onClick = {
                        isSyncing = true
                        coroutineScope.launch {
                            delay(1200)
                            onSyncNow()
                            isSyncing = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoogleBlue),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isSyncing
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Syncing Room DB with Google Cloud...", fontFamily = SFProFontFamily)
                    } else {
                        Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sync Local Room Database", fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                    }
                }

                // Save or Link Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onUnlinkGoogle()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.5f))
                    ) {
                        Text("Unlink", color = CyberRed, fontSize = 12.sp, fontFamily = SFProFontFamily)
                    }

                    Button(
                        onClick = {
                            onSaveGoogleAccount(inputGoogleId, selectedPresetEmail, inputDisplayName)
                            onDismiss()
                        },
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save & Link Google ID", color = DarkBackground, fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = SFProFontFamily)
                    }
                }
            }
        }
    }
}
