package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationEntity
import com.example.notification.MatchNotificationHelper
import com.example.ui.components.PGGlassCard
import com.example.ui.components.PGSecondaryButton
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    viewModel: PGEsportsViewModel,
    onNavigateDeepLink: (String) -> Unit = {},
    onNavigateToPdfDoc: () -> Unit = {}
) {
    val context = LocalContext.current
    val notifications by viewModel.notifications.collectAsState()
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }
    var alarmRemindersEnabled by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NOTIFICATIONS HUB",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp,
                        fontFamily = SFProFontFamily
                    )
                    Text(
                        text = "Real-time match room keys, prize payouts & alerts",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = SFProFontFamily
                    )
                }

                TextButton(onClick = { viewModel.markNotificationsRead() }) {
                    Text("Mark Read", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                }
            }
        }

        // 15-Minute Local Match Alarm Notification System Card
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonGreen,
                backgroundColor = DarkSurfaceVariant
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Alarm, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "15-Min Match Local Alarm",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontFamily = SFProFontFamily
                                )
                                Text(
                                    text = "Automated high-priority device push alerts",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }

                        Switch(
                            checked = alarmRemindersEnabled,
                            onCheckedChange = { alarmRemindersEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DarkBackground,
                                checkedTrackColor = NeonGreen
                            )
                        )
                    }

                    Text(
                        text = "• Fires an exact local alarm 15 minutes before any registered Free Fire match starts.\n" +
                                "• Broadcasts Custom Room ID & Password directly to device notification tray with 1-tap room entry.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp,
                        fontFamily = SFProFontFamily
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                MatchNotificationHelper.show15MinReminderNotification(
                                    context = context,
                                    tournamentId = "TEST_MATCH_001",
                                    tournamentTitle = "FF Grand Finals Bermuda (15m Alert)",
                                    roomId = "9823412",
                                    roomPassword = "PG99",
                                    slotNumber = 12
                                )
                                Toast.makeText(context, "🚨 15-Minute Alarm Push Notification Fired!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = DarkBackground
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Test Match Alarm", fontWeight = FontWeight.Black, fontSize = 11.sp, fontFamily = SFProFontFamily)
                        }
                    }
                }
            }
        }

        // PG E-SPORTS 2.0 Product Spec PDF Banner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToPdfDoc() },
                shape = RoundedCornerShape(12.dp),
                color = DarkCardBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonPurpleLight.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonPurpleDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "PG E-SPORTS 2.0 Spec PDF 📄",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = TextPrimary,
                                fontFamily = SFProFontFamily
                            )
                            Text(
                                text = "Player + Verified Host Architecture, RBAC & MongoDB",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = NeonPurpleLight)
                }
            }
        }

        item {
            Text(
                text = "SYSTEM & MATCH ALERTS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp,
                fontFamily = SFProFontFamily
            )
        }

        if (notifications.isEmpty()) {
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No notifications yet", color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                        Text("You'll receive instant alerts when match rooms open or prizes are credited.", color = TextSecondary, fontSize = 12.sp, fontFamily = SFProFontFamily)
                    }
                }
            }
        } else {
            items(notifications) { notif ->
                val icon = when (notif.category) {
                    "MATCH_ROOM" -> Icons.Default.VpnKey
                    "WALLET" -> Icons.Default.AccountBalanceWallet
                    "TOURNAMENT" -> Icons.Default.EmojiEvents
                    else -> Icons.Default.Notifications
                }
                val iconTint = when (notif.category) {
                    "MATCH_ROOM" -> NeonGreen
                    "WALLET" -> CyberGold
                    "TOURNAMENT" -> NeonPurpleLight
                    else -> ElectricBlue
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (!notif.isRead) DarkSurfaceVariant else DarkCardBg,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (!notif.isRead) iconTint.copy(alpha = 0.5f) else DarkCardBorder
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(iconTint.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = notif.title,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontFamily = SFProFontFamily
                                )
                                Text(
                                    text = dateFormat.format(Date(notif.timestamp)),
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = SFProFontFamily
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notif.message,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }
                }
            }
        }
    }
}
