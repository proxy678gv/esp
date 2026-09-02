package com.example.ui.screens

import androidx.compose.foundation.background
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
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@Composable
fun SupportScreen(
    viewModel: PGEsportsViewModel
) {
    val supportTickets by viewModel.supportTickets.collectAsState()
    val allTournaments by viewModel.allTournaments.collectAsState()

    var showNewTicketDialog by remember { mutableStateOf(false) }
    var showDisputeDialog by remember { mutableStateOf(false) }

    // Support Form States
    var ticketSubjectInput by remember { mutableStateOf("") }
    var ticketCategoryInput by remember { mutableStateOf("PAYOUT") }
    var ticketMessageInput by remember { mutableStateOf("") }

    // Dispute Form States
    var disputeTournamentInput by remember { mutableStateOf(allTournaments.firstOrNull()?.title ?: "Free Fire Match") }
    var disputeCategoryInput by remember { mutableStateOf("CHEATING") }
    var disputeDescriptionInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Action Cards
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberOrange,
                backgroundColor = DarkCardBg
            ) {
                Text(
                    text = "SUPPORT & DISPUTE RESOLUTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberOrange,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Official 24/7 Referee Desk",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Report match cheating, scoring errors, or submit payout assistance tickets.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PGNeonButton(
                        text = "File Match Dispute 🛡️",
                        onClick = { showDisputeDialog = true },
                        modifier = Modifier.weight(1f),
                        containerColor = CyberOrange,
                        glowColor = CyberOrange
                    )

                    PGSecondaryButton(
                        text = "New Ticket 💬",
                        onClick = { showNewTicketDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Support Tickets
        item {
            Text(
                text = "MY SUPPORT TICKETS (${supportTickets.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        if (supportTickets.isEmpty()) {
            item {
                Text("No support tickets raised.", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            items(supportTickets) { ticket ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(ticket.subject, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
                            Surface(
                                color = if (ticket.status == "RESOLVED") NeonGreen.copy(alpha = 0.2f) else CyberGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = ticket.status,
                                    color = if (ticket.status == "RESOLVED") NeonGreen else CyberGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(ticket.message, color = TextSecondary, fontSize = 12.sp)

                        if (ticket.adminReply.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = DarkBackground
                            ) {
                                Text(
                                    text = "💬 Official Admin Response: ${ticket.adminReply}",
                                    color = NeonGreenLight,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Support Ticket Dialog
    if (showNewTicketDialog) {
        AlertDialog(
            onDismissRequest = { showNewTicketDialog = false },
            containerColor = DarkSurface,
            title = { Text("Open Support Ticket 💬", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = ticketSubjectInput, onValueChange = { ticketSubjectInput = it }, label = { Text("Subject (e.g. UPI Payout Status)") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = ticketMessageInput, onValueChange = { ticketMessageInput = it }, label = { Text("Describe your query in detail") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Submit Ticket",
                    enabled = ticketSubjectInput.isNotBlank() && ticketMessageInput.isNotBlank(),
                    onClick = {
                        viewModel.submitSupportTicket(ticketSubjectInput, ticketCategoryInput, ticketMessageInput)
                        showNewTicketDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showNewTicketDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }

    // Dispute Dialog
    if (showDisputeDialog) {
        AlertDialog(
            onDismissRequest = { showDisputeDialog = false },
            containerColor = DarkSurface,
            title = { Text("Report Match Violation / Dispute 🛡️", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = disputeTournamentInput, onValueChange = { disputeTournamentInput = it }, label = { Text("Tournament Name / ID") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = disputeDescriptionInput, onValueChange = { disputeDescriptionInput = it }, label = { Text("Violation Details (e.g. Illegal emulator usage / wrong kill score)") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                    Text("💡 All reports are reviewed with in-game logs & video replays.", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Submit Dispute",
                    enabled = disputeDescriptionInput.isNotBlank(),
                    onClick = {
                        viewModel.submitDispute(
                            tournamentId = "TOUR_ACTIVE",
                            tournamentTitle = disputeTournamentInput,
                            category = disputeCategoryInput,
                            description = disputeDescriptionInput
                        )
                        showDisputeDialog = false
                    },
                    containerColor = CyberOrange,
                    glowColor = CyberOrange
                )
            },
            dismissButton = {
                TextButton(onClick = { showDisputeDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}
