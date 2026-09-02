package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.PGGlassCard
import com.example.ui.components.PGNeonButton
import com.example.ui.theme.*
import com.example.util.PdfDocumentExporter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfDocScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var selectedPage by remember { mutableStateOf(0) }
    var isExportingPdf by remember { mutableStateOf(false) }

    val pageTitles = listOf("1. Concept & Flows", "2. Host Hub & RBAC", "3. Payment & Schemas")

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            Surface(
                color = DarkBackground,
                border = BorderStroke(0.5.dp, DarkCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                Text(
                                    text = "PG E-SPORTS 2.0",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = TextPrimary,
                                    fontFamily = SFProFontFamily
                                )
                                Text(
                                    text = "Official Product Specification PDF",
                                    fontSize = 11.sp,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }

                        // Export / Share PDF Action Button
                        Button(
                            onClick = {
                                isExportingPdf = true
                                val pdfFile = PdfDocumentExporter.generateProductSpecPdf(context)
                                isExportingPdf = false
                                if (pdfFile != null && pdfFile.exists()) {
                                    Toast.makeText(context, "PDF generated successfully! (${pdfFile.length() / 1024} KB)", Toast.LENGTH_SHORT).show()
                                    PdfDocumentExporter.shareGeneratedPdf(context, pdfFile)
                                } else {
                                    Toast.makeText(context, "Failed to generate PDF.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPurpleDark,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            border = BorderStroke(1.dp, NeonPurpleLight)
                        ) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Page Tabs Switcher
                    TabRow(
                        selectedTabIndex = selectedPage,
                        containerColor = DarkSurfaceVariant,
                        contentColor = NeonGreen,
                        divider = {}
                    ) {
                        pageTitles.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedPage == index,
                                onClick = { selectedPage = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedPage == index) FontWeight.Black else FontWeight.Normal,
                                        fontFamily = SFProFontFamily
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner Card
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonGreen,
                backgroundColor = DarkSurface
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NeonGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.5.dp, NeonGreen)
                        ) {
                            Text(
                                text = "TECHNICAL SPECIFICATION",
                                color = NeonGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontFamily = SFProFontFamily
                            )
                        }

                        Text("Version 2.0 • Garena Free Fire", fontSize = 10.sp, color = TextMuted, fontFamily = SFProFontFamily)
                    }

                    Text(
                        text = "PG E-SPORTS 2.0",
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = TextPrimary,
                        fontFamily = SFProFontFamily
                    )
                    Text(
                        text = "Player + Verified Host Tournament Platform",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ElectricBlue,
                        fontFamily = SFProFontFamily
                    )
                    Text(
                        text = "Tagline: Play. Compete. Win.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = CyberGold,
                        fontFamily = SFProFontFamily
                    )
                }
            }

            when (selectedPage) {
                0 -> PageOneContent()
                1 -> PageTwoContent()
                2 -> PageThreeContent(
                    onCopyCode = { code ->
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "Schema copied to clipboard!", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            // Bottom CTA to generate real PDF
            Button(
                onClick = {
                    val pdfFile = PdfDocumentExporter.generateProductSpecPdf(context)
                    if (pdfFile != null && pdfFile.exists()) {
                        Toast.makeText(context, "PDF saved to: ${pdfFile.name}", Toast.LENGTH_SHORT).show()
                        PdfDocumentExporter.shareGeneratedPdf(context, pdfFile)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = DarkBackground)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate & Download Complete PDF File", color = DarkBackground, fontWeight = FontWeight.Black, fontFamily = SFProFontFamily)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PageOneContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Core Concept
        DocSectionCard(
            title = "1. Core Concept",
            badge = "SINGLE ACCOUNT RBAC",
            badgeColor = NeonGreen
        ) {
            Text(
                text = "The platform utilizes a single normal user account architecture with fine-grained role-based permissions.\n\n" +
                        "• Every new user starts as a PLAYER.\n" +
                        "• Players can browse Free Fire tournaments, view entry fees & prize pools, join tournaments, pay entry fees, receive slots, access Room ID/Password according to tournament rules, play the match, view leaderboards, and receive verified winnings.\n" +
                        "• A player can apply to become a HOST. The Super Admin reviews the application. Only approved users receive Host Mode.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp,
                fontFamily = SFProFontFamily
            )
        }

        // Account Flow Diagram
        DocSectionCard(
            title = "2. Account Flow Diagram",
            badge = "LIFECYCLE TRANSITION",
            badgeColor = NeonPurpleLight
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DiagramStep("PLAYER ACCOUNT", "Default registration state for all Free Fire gamers", NeonGreen)
                DiagramArrow()
                DiagramStep("Apply for Host", "Submit UID, experience & monthly tournament forecast", ElectricBlue)
                DiagramArrow()
                DiagramStep("Super Admin Review", "Manual & automated security verification", CyberGold)
                DiagramArrow()
                DiagramStep("Approved", "Host credentials validated & cryptographic ID generated", NeonPurpleLight)
                DiagramArrow()
                DiagramStep("HOST MODE UNLOCKED", "Switch freely between 🎮 Player Mode and 🛡️ Host Mode", NeonGreen)
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "✨ A Host can still participate in tournaments as a normal player without logging into a separate account.",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                fontFamily = SFProFontFamily
            )
        }

        // Player Tournament Workflow
        DocSectionCard(
            title = "3. Player Tournament Workflow",
            badge = "STEP-BY-STEP PROCESS",
            badgeColor = ElectricBlue
        ) {
            val workflowSteps = listOf(
                "Browse Tournament" to "Explore active Solo, Duo, Squad Free Fire matches with filters",
                "Select Tournament" to "Examine prize pool breakdown, per-kill bounties, Booyah reward & rules",
                "View Entry Fee + Rules" to "Review 15-minute check-in mandate and fair play terms",
                "JOIN NOW" to "Select registered squad & captain confirmation",
                "Payment Gateway (Razorpay)" to "UPI Fast Checkout, Cards, NetBanking, or PG Diamond Vault",
                "Payment Successful" to "Escrow ledger entry recorded & slot allocated",
                "Tournament Slot Confirmed" to "Automated 15-minute local notification alarm scheduled",
                "Room ID/Password Released" to "Credentials revealed 15 minutes before start time",
                "Play Match" to "Compete on Garena Free Fire (Bermuda/Purgatory)",
                "Results Verified" to "Automated match audit & kill placement scoring",
                "Prize Credited" to "Instant deposit to PG Wallet; 100% withdrawable to UPI"
            )

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                workflowSteps.forEachIndexed { idx, (step, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkCardBg, RoundedCornerShape(8.dp))
                            .border(BorderStroke(0.5.dp, DarkCardBorder), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = NeonGreen.copy(alpha = 0.2f),
                            shape = CircleShape,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("${idx + 1}", fontSize = 10.sp, fontWeight = FontWeight.Black, color = NeonGreen, fontFamily = SFProFontFamily)
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(step, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                            Text(desc, fontSize = 10.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PageTwoContent() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Host Permissions & Prohibitions
        DocSectionCard(
            title = "4. Host Permissions & Operational Boundaries",
            badge = "ACCESS CONTROL",
            badgeColor = CyberGold
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = NeonGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("✅ APPROVED HOSTS CAN:", fontWeight = FontWeight.Black, fontSize = 11.sp, color = NeonGreen, fontFamily = SFProFontFamily)
                        Text("• Create tournaments & Edit their own tournaments", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Set tournament title & select Free Fire type (Solo / Duo / Squad)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Set entry fee, guaranteed prize pool & maximum participants", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Configure kill rewards, Booyah rewards & custom match rules", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Set date, time, manage participants & assign slots", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Add Custom Room ID and Room Password", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Publish tournament & manage tournament results", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                    }
                }

                Surface(
                    color = CyberRed.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, CyberRed.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("⛔ HOSTS MUST NOT BE ABLE TO (RESTRICTIONS):", fontWeight = FontWeight.Black, fontSize = 11.sp, color = CyberRed, fontFamily = SFProFontFamily)
                        Text("• Edit another host's tournament (Ownership rule enforced)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Access Super Admin settings or approve other hosts", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Access platform financial administration or modify payment records", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                        Text("• Withdraw player entry-fee funds directly (Escrow Protection)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                    }
                }
            }
        }

        // Host Tournament Creation Dashboard & Status Progression
        DocSectionCard(
            title = "5. Host Tournament Dashboard & Creation",
            badge = "LIFECYCLE STATUSES",
            badgeColor = NeonPurpleLight
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Dashboard Metrics: Total Earnings • Total Tournaments • Active Tournaments • Completed Tournaments • Total Participants • [CREATE TOURNAMENT]",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberGold,
                    fontFamily = SFProFontFamily
                )

                Text(
                    text = "Tournament Creation Fields:\n" +
                            "Tournament Name • Game (Free Fire) • Tournament Type (Solo / Duo / Squad) • Entry Fee • Prize Pool • Max Participants • Per Kill Reward • Booyah Reward • Date • Time • Rules • Room ID • Room Password • Tournament Status",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    fontFamily = SFProFontFamily
                )

                Divider(color = DarkCardBorder)

                Text("Lifecycle Status Progression:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                val statuses = listOf("Draft", "Published", "Registration Open", "Full", "Live", "Completed", "Results Verified")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    statuses.forEachIndexed { i, s ->
                        Surface(
                            color = DarkCardBg,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(0.5.dp, if (i == statuses.lastIndex) NeonGreen else DarkCardBorder)
                        ) {
                            Text(
                                text = if (i < statuses.lastIndex) "$s →" else s,
                                color = if (i == statuses.lastIndex) NeonGreen else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontFamily = SFProFontFamily
                            )
                        }
                    }
                }
            }
        }

        // Role Permissions Matrix Table
        DocSectionCard(
            title = "6. Role System Permissions Matrix",
            badge = "PLAYER vs HOST vs ADMIN",
            badgeColor = ElectricBlue
        ) {
            val tableRows = listOf(
                listOf("Browse & Join Tournaments", "✅", "✅", "✅"),
                listOf("Make Payments (Razorpay)", "✅", "✅", "✅"),
                listOf("15-Min Match Local Reminders", "✅", "✅", "✅"),
                listOf("Receive Winnings in Wallet", "✅", "✅", "✅"),
                listOf("Create & Edit Own Tournaments", "❌", "✅", "✅"),
                listOf("Manage Room ID & Password", "❌", "✅", "✅"),
                listOf("Publish Match Results", "❌", "✅", "✅"),
                listOf("Approve / Reject / Suspend Hosts", "❌", "❌", "✅"),
                listOf("Platform Financial Administration", "❌", "❌", "✅")
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.dp, DarkCardBorder), RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceVariant)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Capability", fontWeight = FontWeight.Black, fontSize = 11.sp, color = TextPrimary, modifier = Modifier.weight(1.8f), fontFamily = SFProFontFamily)
                    Text("Player", fontWeight = FontWeight.Black, fontSize = 11.sp, color = NeonGreen, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center, fontFamily = SFProFontFamily)
                    Text("Host", fontWeight = FontWeight.Black, fontSize = 11.sp, color = CyberGold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center, fontFamily = SFProFontFamily)
                    Text("Admin", fontWeight = FontWeight.Black, fontSize = 11.sp, color = NeonPurpleLight, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center, fontFamily = SFProFontFamily)
                }

                tableRows.forEachIndexed { idx, row ->
                    Divider(color = DarkCardBorder)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (idx % 2 == 0) DarkCardBg else DarkSurface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(row[0], fontSize = 10.sp, color = TextSecondary, modifier = Modifier.weight(1.8f), fontFamily = SFProFontFamily)
                        Text(row[1], fontSize = 11.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                        Text(row[2], fontSize = 11.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                        Text(row[3], fontSize = 11.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
private fun PageThreeContent(
    onCopyCode: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Payment Architecture
        DocSectionCard(
            title = "7. Payment Architecture & Escrow Security",
            badge = "RAZORPAY INTEGRATION",
            badgeColor = NeonGreen
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = Color(0xFF0C2340),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF3395FF).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "PLAYER  →  PAYMENT GATEWAY (RAZORPAY)  →  PG E-SPORTS BACKEND / TRANSACTION LEDGER  →  TOURNAMENT ENTRY CONFIRMATION  →  MATCH  →  RESULT VERIFICATION  →  WINNER BALANCE  →  WITHDRAWAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3395FF),
                        modifier = Modifier.padding(10.dp),
                        lineHeight = 15.sp,
                        fontFamily = SFProFontFamily
                    )
                }

                Text(
                    text = "🔒 Important Security Rule:\n" +
                            "Player payments must NOT be directly controlled by tournament Hosts. The platform backend maintains an authoritative immutable transaction ledger with cryptographic IDs, preventing embezzlement or unauthorized payouts.",
                    fontSize = 11.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp,
                    fontFamily = SFProFontFamily
                )
            }
        }

        // Backend Security & RBAC Enforcement
        DocSectionCard(
            title = "8. Backend Security & RBAC Enforcement",
            badge = "OWNERSHIP VALIDATION",
            badgeColor = CyberRed
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Permissions must be enforced on the backend, not only hidden in the UI layer. For every Host mutation:\n\n" +
                            "Authentication Check (JWT Bearer Token)\n" +
                            "  ↓\n" +
                            "Host Approval Check (isHostApproved == true)\n" +
                            "  ↓\n" +
                            "Tournament Ownership Check (loggedInUser.id == tournament.hostId)\n" +
                            "  ↓\n" +
                            "Allow / Reject Request",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp,
                    fontFamily = FontFamily.Monospace
                )

                Surface(
                    color = CyberGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "A Host can edit a tournament only when: loggedInUser.id == tournament.hostId. This strictly prevents cross-host tampering.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberGold,
                        modifier = Modifier.padding(8.dp),
                        fontFamily = SFProFontFamily
                    )
                }
            }
        }

        // MongoDB & Room Database Schemas
        DocSectionCard(
            title = "9. Database Schemas (MongoDB & Room SQLite)",
            badge = "DATA MODELS",
            badgeColor = NeonPurpleLight
        ) {
            val schemaText = """
// 1. USER SCHEMA
{
  "_id": "ObjectId",
  "googleId": "1098472910398471029",
  "email": "player@pgesports.com",
  "inGameId": "PG_THUNDER_99",
  "role": "PLAYER" | "HOST" | "ADMIN",
  "hostStatus": "NONE" | "PENDING" | "APPROVED" | "SUSPENDED",
  "walletBalance": 1250.00,
  "rating": 2840,
  "rankTier": "HEROIC"
}

// 2. TOURNAMENT SCHEMA
{
  "_id": "ObjectId",
  "hostId": "ObjectId (User)",
  "title": "FF Grand Finals Bermuda",
  "game": "FREE_FIRE",
  "type": "SOLO" | "DUO" | "SQUAD",
  "entryFee": 50.00,
  "prizePool": 2000.00,
  "maxParticipants": 48,
  "perKillReward": 10.00,
  "booyahReward": 1000.00,
  "scheduleMillis": 1756820000000,
  "roomId": "9823412",
  "roomPassword": "PG99",
  "status": "DRAFT" | "REGISTRATION_OPEN" | "FULL" | "LIVE" | "COMPLETED" | "VERIFIED"
}

// 3. TOURNAMENT PARTICIPANT SCHEMA
{
  "_id": "ObjectId",
  "tournamentId": "ObjectId",
  "playerId": "ObjectId",
  "teamId": "ObjectId",
  "slotNumber": 12,
  "paymentId": "pay_RZP_99214",
  "isCheckInDone": true,
  "kills": 4,
  "placement": 1
}

// 4. TRANSACTION LEDGER SCHEMA
{
  "_id": "ObjectId",
  "userId": "ObjectId",
  "tournamentId": "ObjectId",
  "type": "ENTRY_FEE" | "PRIZE_PAYOUT" | "DEPOSIT" | "WITHDRAWAL",
  "amount": 50.00,
  "status": "COMPLETED",
  "gatewayId": "razorpay_order_9912",
  "timestamp": 1756819000000
}
            """.trimIndent()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("MongoDB BSON / SQLite Entities", fontSize = 11.sp, color = TextMuted, fontFamily = SFProFontFamily)
                    TextButton(onClick = { onCopyCode(schemaText) }) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Schema", color = ElectricBlue, fontSize = 11.sp, fontFamily = SFProFontFamily)
                    }
                }

                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = schemaText,
                        fontSize = 9.5.sp,
                        color = Color(0xFF00FF66),
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Technology Stack
        DocSectionCard(
            title = "10. Technology Stack Summary",
            badge = "ARCHITECTURE",
            badgeColor = ElectricBlue
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("• Mobile Frontend: Android Jetpack Compose & Kotlin (M3 Dynamic + SF Pro Typography)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                Text("• Backend API: Node.js + Express REST API with JWT Bearer Authentication", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                Text("• Persistence Engine: MongoDB (Cloud Cloud DB) + Room SQLite (Local Client DB)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                Text("• Payment Gateway: Razorpay SDK (UPI Fast Apps, Cards, NetBanking, Escrow Ledger)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                Text("• Access Control: Role-Based Access Control (RBAC) [PLAYER / HOST / SUPER ADMIN]", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
                Text("• Notifications: Android AlarmManager & NotificationChannel (15-Min Match Alert)", fontSize = 11.sp, color = TextPrimary, fontFamily = SFProFontFamily)
            }
        }
    }
}

@Composable
private fun DocSectionCard(
    title: String,
    badge: String,
    badgeColor: Color,
    content: @Composable () -> Unit
) {
    PGGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = DarkCardBorder,
        backgroundColor = DarkCardBg
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = TextPrimary,
                    fontFamily = SFProFontFamily
                )
                Surface(
                    color = badgeColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(0.5.dp, badgeColor)
                ) {
                    Text(
                        text = badge,
                        color = badgeColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontFamily = SFProFontFamily
                    )
                }
            }
            Divider(color = DarkCardBorder)
            content()
        }
    }
}

@Composable
private fun DiagramStep(title: String, subtitle: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkBackground, RoundedCornerShape(8.dp))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.5f)), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(title, fontWeight = FontWeight.Black, fontSize = 12.sp, color = color, fontFamily = SFProFontFamily)
            Text(subtitle, fontSize = 10.sp, color = TextSecondary, fontFamily = SFProFontFamily)
        }
    }
}

@Composable
private fun DiagramArrow() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
    }
}
