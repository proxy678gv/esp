package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminConsoleScreen(
    viewModel: PGEsportsViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val hostApplications by viewModel.hostApplications.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val allTournaments by viewModel.allTournaments.collectAsState()
    val disputeReports by viewModel.disputeReports.collectAsState()

    var selectedAdminTab by remember { mutableStateOf(0) } // 0: Overview, 1: Host Applications, 2: Disputes, 3: Audit Logs
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm:ss a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- 1. RBAC Multi-Role Switcher Bar ---
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberGold,
                backgroundColor = DarkCardBg
            ) {
                Text(
                    text = "👑 ROLE SWITCHER & PERMISSION CONSOLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberGold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Current Active Role: ${user?.role?.name ?: "PLAYER"}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = user?.role == role
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.switchRole(role) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) CyberGold else DarkSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) CyberGold else DarkCardBorder
                            )
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = role.name.replace("_", " "),
                                    color = if (isSelected) DarkBackground else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Admin Sub-Tabs ---
        item {
            val tabs = listOf("📊 Overview", "📋 Host Apps (${hostApplications.count { it.status == "PENDING" }})", "⚖️ Disputes (${disputeReports.size})", "📜 Audit Logs")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(tabs.indices.toList()) { index ->
                    val isSelected = selectedAdminTab == index
                    Surface(
                        modifier = Modifier.clickable { selectedAdminTab = index },
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonGreen else DarkSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonGreen else DarkCardBorder
                        )
                    ) {
                        Text(
                            text = tabs[index],
                            color = if (isSelected) DarkBackground else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // --- Tab Contents ---
        when (selectedAdminTab) {
            0 -> {
                // Platform Metrics Overview
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(title = "Total Tournaments", value = "${allTournaments.size}", color = ElectricBlue, modifier = Modifier.weight(1f))
                            MetricCard(title = "Live Tournaments", value = "${allTournaments.count { it.status == TournamentStatus.LIVE }}", color = CyberRed, modifier = Modifier.weight(1f))
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(title = "Prize Volume", value = "₹65,000", color = CyberGold, modifier = Modifier.weight(1f))
                            MetricCard(title = "Pending Hosts", value = "${hostApplications.count { it.status == "PENDING" }}", color = NeonPurpleLight, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            1 -> {
                // Host Applications Verification Desk
                items(hostApplications) { app ->
                    PGGlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (app.status == "PENDING") CyberGold else DarkCardBorder,
                        backgroundColor = DarkCardBg
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(app.fullName, fontWeight = FontWeight.Black, fontSize = 15.sp, color = TextPrimary)
                            Surface(
                                color = if (app.status == "APPROVED") NeonGreen.copy(alpha = 0.2f) else CyberGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = app.status,
                                    color = if (app.status == "APPROVED") NeonGreen else CyberGold,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Location: ${app.city} • Mobile: ${app.mobile}", color = TextSecondary, fontSize = 12.sp)
                        Text("Experience: ${app.gamingExperience}", color = TextSecondary, fontSize = 12.sp)
                        Text("Reason: ${app.reason}", color = TextPrimary, fontSize = 12.sp)

                        if (app.status == "PENDING") {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                PGNeonButton(
                                    text = "Approve Host ✅",
                                    onClick = { },
                                    modifier = Modifier.weight(1f)
                                )
                                PGSecondaryButton(
                                    text = "Reject",
                                    onClick = { },
                                    borderColor = CyberRed,
                                    modifier = Modifier.weight(0.7f)
                                )
                            }
                        }
                    }
                }
            }

            2 -> {
                // Disputes Center
                items(disputeReports) { report ->
                    PGGlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = DarkCardBg) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Dispute: ${report.category}", fontWeight = FontWeight.Bold, color = CyberOrange)
                            Text(report.status, color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(report.description, color = TextPrimary, fontSize = 13.sp)
                        Text("Reported by ${report.reporterName} on ${report.tournamentTitle}", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            3 -> {
                // System Audit Logs
                items(auditLogs) { log ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = DarkSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "[${log.actorRole}] ${log.action}",
                                    fontWeight = FontWeight.Bold,
                                    color = NeonGreen,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = dateFormat.format(Date(log.timestamp)),
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(log.details, color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    PGGlassCard(
        modifier = modifier,
        borderColor = color,
        backgroundColor = DarkCardBg
    ) {
        Text(title, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Black)
    }
}
