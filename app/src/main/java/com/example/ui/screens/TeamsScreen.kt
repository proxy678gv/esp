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
import com.example.data.model.TeamEntity
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@Composable
fun TeamsScreen(
    viewModel: PGEsportsViewModel
) {
    val userTeams by viewModel.userTeams.collectAsState()
    val allTeams by viewModel.allTeams.collectAsState()

    var showCreateTeamDialog by remember { mutableStateOf(false) }
    var teamNameInput by remember { mutableStateOf("") }
    var teamTagInput by remember { mutableStateOf("") }
    var teamGameInput by remember { mutableStateOf("Free Fire") }
    var teamMembersInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Action Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESPORTS SQUADS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Manage your team roster for official tournaments",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                PGNeonButton(
                    text = "+ Create Squad",
                    onClick = { showCreateTeamDialog = true }
                )
            }
        }

        // User's Squads
        item {
            Text(
                text = "MY REGISTERED SQUADS (${userTeams.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = NeonGreen,
                letterSpacing = 1.sp
            )
        }

        if (userTeams.isEmpty()) {
            item {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = DarkSurface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.GroupAdd, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("You don't have an active squad yet.", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Text("Create a squad and add your 4 players to participate in tournament matches.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(userTeams) { team ->
                TeamCard(team = team, isMyTeam = true)
            }
        }

        // All Platform Esports Squads
        item {
            Text(
                text = "ALL ACTIVE ESPORTS CLANS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        items(allTeams.filter { it.captainId != viewModel.currentUser.value?.id }) { team ->
            TeamCard(team = team, isMyTeam = false)
        }
    }

    // Create Team Dialog
    if (showCreateTeamDialog) {
        AlertDialog(
            onDismissRequest = { showCreateTeamDialog = false },
            containerColor = DarkSurface,
            title = { Text("Create Esports Squad 🎮", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = teamNameInput,
                        onValueChange = { teamNameInput = it },
                        label = { Text("Squad Name (e.g. Phoenix Titans)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = teamTagInput,
                        onValueChange = { teamTagInput = it },
                        label = { Text("Clan Tag (e.g. PT9)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = teamGameInput,
                        onValueChange = { teamGameInput = it },
                        label = { Text("Game (Free Fire / BGMI / Valorant)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = teamMembersInput,
                        onValueChange = { teamMembersInput = it },
                        label = { Text("Player Usernames (comma separated)") },
                        placeholder = { Text("Player1, Player2, Player3, Player4") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Save Squad",
                    enabled = teamNameInput.isNotBlank() && teamTagInput.isNotBlank(),
                    onClick = {
                        viewModel.createTeam(
                            name = teamNameInput.trim(),
                            tag = teamTagInput.trim().uppercase(),
                            game = teamGameInput.trim(),
                            members = if (teamMembersInput.isBlank()) "ThunderGod_PG, ViperStrike, SniperTamil" else teamMembersInput.trim()
                        )
                        showCreateTeamDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showCreateTeamDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun TeamCard(team: TeamEntity, isMyTeam: Boolean) {
    PGGlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isMyTeam) NeonGreen else DarkCardBorder,
        backgroundColor = DarkCardBg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = NeonPurple.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "[${team.tag}]",
                        color = NeonPurpleLight,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(team.name, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Text("Game: ${team.game} • Captain: ${team.captainName}", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text("${team.totalWins} Wins", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                Text("${team.totalMatches} Matches", color = TextSecondary, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Divider(color = DarkCardBorder)
        Spacer(modifier = Modifier.height(8.dp))

        Text("ROSTER:", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
        Text(team.memberNames, color = TextPrimary, fontSize = 12.sp)
    }
}
