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
import com.example.data.model.TournamentEntity
import com.example.ui.components.PGGlassCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen(
    viewModel: PGEsportsViewModel,
    onSelectTournament: (TournamentEntity) -> Unit
) {
    val tournaments by viewModel.filteredTournaments.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedGame by viewModel.selectedGameFilter.collectAsState()
    val selectedStatus by viewModel.selectedStatusFilter.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // --- Search Bar ---
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search tournaments, games, maps...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonGreen) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonGreen,
                unfocusedBorderColor = DarkCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // --- Game Filter Chips ---
        val gameFilters = listOf(
            "ALL" to "All Games",
            "FREE_FIRE" to "Free Fire",
            "BGMI" to "BGMI",
            "VALORANT_MOBILE" to "Valorant",
            "CALL_OF_DUTY" to "CODM"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(gameFilters) { (id, label) ->
                val isSelected = selectedGame == id
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedGameFilter.value = id },
                    label = {
                        Text(
                            text = label,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) DarkBackground else TextPrimary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonGreen,
                        containerColor = DarkSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = DarkCardBorder,
                        selectedBorderColor = NeonGreen
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- Status Tabs (All, Live, Open, Check-In, Completed) ---
        val statusFilters = listOf(
            "ALL" to "All",
            "LIVE" to "🔴 Live",
            "REGISTRATION_OPEN" to "🟢 Reg Open",
            "CHECK_IN" to "⏱️ Check-In",
            "COMPLETED" to "🏆 Completed"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(statusFilters) { (id, label) ->
                val isSelected = selectedStatus == id
                Surface(
                    modifier = Modifier.clickable { viewModel.selectedStatusFilter.value = id },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) NeonPurple else DarkSurface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NeonPurpleLight else DarkCardBorder
                    )
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- Tournaments List ---
        if (tournaments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    backgroundColor = DarkSurface
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Tournaments Found",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing your search query or switching game filters.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tournaments) { tournament ->
                    TournamentCard(
                        tournament = tournament,
                        onSelect = { onSelectTournament(tournament) },
                        onJoin = { onSelectTournament(tournament) }
                    )
                }
            }
        }
    }
}
