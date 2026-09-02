package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.localization.AppLanguage
import com.example.data.localization.PGStrings
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R

@Composable
fun HomeScreen(
    viewModel: PGEsportsViewModel,
    onNavigateToTournaments: () -> Unit,
    onNavigateToFreeFireHub: () -> Unit,
    onSelectTournament: (TournamentEntity) -> Unit,
    onNavigateToMatchRoom: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToRank: () -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val allTournaments by viewModel.allTournaments.collectAsState()
    val userRegistrations by viewModel.userRegistrations.collectAsState()
    val language by viewModel.language.collectAsState()

    val featuredTournaments = remember(allTournaments) {
        allTournaments.filter { it.isFeatured || it.status == TournamentStatus.LIVE }
    }

    val liveTournaments = remember(allTournaments) {
        allTournaments.filter { it.status == TournamentStatus.LIVE }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // --- 1. Free Fire 4K Featured Hero Showcase Card ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onNavigateToFreeFireHub() },
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                border = BorderStroke(1.dp, CyberOrange.copy(alpha = 0.6f))
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ff_hero_banner),
                        contentDescription = "Free Fire Booyah Hub 4K",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, DarkBackground.copy(alpha = 0.95f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = CyberOrange,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "🔥 FREE FIRE META 4K",
                                    color = DarkBackground,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                color = DarkBackground.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(6.dp),
                                border = BorderStroke(1.dp, CyberGold)
                            ) {
                                Text(
                                    text = "💎 DIAMOND VAULT",
                                    color = CyberGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Characters • Evo Guns • Tactical Pets",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = "Explore 4K stats, armor penetration specs, and instant diamond top-ups.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onNavigateToFreeFireHub() },
                                color = CyberOrange,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Explore Free Fire Hub 🚀",
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 2. Esports User Stats Banner (Glassmorphic) ---
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                cornerRadius = 20.dp,
                borderColor = NeonGreen,
                backgroundColor = DarkSurfaceVariant.copy(alpha = 0.85f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = NeonGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "PG E-SPORTS 2.0",
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (liveTournaments.isNotEmpty()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    PGLivePulseIndicator()
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${liveTournaments.size} LIVE NOW",
                                        color = CyberRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = PGStrings.appTagline(language),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            lineHeight = 26.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tamil Nadu's #1 Cloud Esports Platform with instant verified UPI payouts.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick User Stats Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkBackground.copy(alpha = 0.6f))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // PG Rank
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateToRank() }
                    ) {
                        Text("PG RANK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        user?.let { PGRankBadge(tier = it.rankTier, rating = it.rating) }
                    }

                    Divider(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp),
                        color = DarkCardBorder
                    )

                    // Wallet Balance
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { onNavigateToWallet() }
                    ) {
                        Text("WALLET", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "₹${user?.walletBalance?.toInt() ?: 0}",
                            color = NeonGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(28.dp)
                            .width(1.dp),
                        color = DarkCardBorder
                    )

                    // Win Rate
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("WIN RATE", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${user?.winRatePercent ?: 0f}%",
                            color = ElectricBlue,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // --- 2. Active Match / Check-In Action Alert (If Registered) ---
        if (userRegistrations.isNotEmpty()) {
            item {
                val activeReg = userRegistrations.first()
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonPurple,
                    backgroundColor = DarkSurface.copy(alpha = 0.9f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(NeonPurple.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SportsEsports,
                                    contentDescription = null,
                                    tint = NeonPurpleLight
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Active Registered Match",
                                    fontSize = 11.sp,
                                    color = NeonPurpleLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = activeReg.tournamentTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Squad: ${activeReg.teamName} (Slot #${activeReg.slotNumber})",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PGNeonButton(
                            text = "Enter Match Room 🔑",
                            onClick = { onNavigateToMatchRoom(activeReg.tournamentId) },
                            modifier = Modifier.weight(1f),
                            glowColor = NeonPurple,
                            containerColor = NeonPurple,
                            contentColor = TextPrimary
                        )
                    }
                }
            }
        }

        // --- 3. Games Category Selector ---
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POPULAR ESPORTS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonGreen,
                        modifier = Modifier.clickable { onNavigateToTournaments() }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val games = listOf(
                        Triple("Free Fire", "Garena • Battle Royale & Clash Squad", NeonGreen),
                        Triple("BGMI", "Krafton • Erangel / Miramar", CyberOrange),
                        Triple("Valorant Mobile", "Riot Games • 5v5 Tactical", CyberRed),
                        Triple("COD Mobile", "Activision • Multiplayer / BR", ElectricBlue)
                    )

                    items(games) { (name, subtitle, color) ->
                        Surface(
                            modifier = Modifier
                                .width(200.dp)
                                .clickable {
                                    viewModel.selectedGameFilter.value = when (name) {
                                        "Free Fire" -> "FREE_FIRE"
                                        "BGMI" -> "BGMI"
                                        "Valorant Mobile" -> "VALORANT_MOBILE"
                                        else -> "CALL_OF_DUTY"
                                    }
                                    onNavigateToTournaments()
                                },
                            shape = RoundedCornerShape(16.dp),
                            color = DarkCardBg,
                            border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gamepad,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Surface(
                                        color = color.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = "Active",
                                            color = color,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = subtitle,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 4. Featured & Live Tournaments ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEATURED TOURNAMENTS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${allTournaments.size} Available",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        items(featuredTournaments) { tournament ->
            TournamentCard(
                tournament = tournament,
                onSelect = { onSelectTournament(tournament) },
                onJoin = { onSelectTournament(tournament) }
            )
        }
    }
}

@Composable
fun TournamentCard(
    tournament: TournamentEntity,
    onSelect: () -> Unit,
    onJoin: () -> Unit
) {
    PGGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        cornerRadius = 16.dp,
        borderColor = if (tournament.status == TournamentStatus.LIVE) CyberRed else DarkCardBorder,
        backgroundColor = DarkCardBg
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PGStatusBadge(status = tournament.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tournament.gameName,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tournament.title,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Info Chips (Map, Format, Mode)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "🗺️ ${tournament.mapName}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Surface(
                color = DarkSurfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "👥 ${tournament.format.name.replace("_", " ")}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Divider(color = DarkCardBorder)
        Spacer(modifier = Modifier.height(12.dp))

        // Prize Pool & Slots Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("PRIZE POOL", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Text(
                    text = "₹${tournament.prizePool.toInt()}",
                    color = CyberGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
                Text(
                    text = "Entry: ${if (tournament.entryFee == 0.0) "FREE" else "₹" + tournament.entryFee.toInt()}",
                    color = if (tournament.entryFee == 0.0) NeonGreen else TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${tournament.registeredTeamsCount}/${tournament.maxTeams} Slots Filled",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Slots Progress Bar
                val progress = (tournament.registeredTeamsCount.toFloat() / tournament.maxTeams.toFloat()).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .width(100.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (progress > 0.8f) CyberRed else NeonGreen,
                    trackColor = DarkSurfaceVariant,
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PGSecondaryButton(
                text = "Details & Rules",
                onClick = onSelect,
                modifier = Modifier.weight(1f)
            )

            PGNeonButton(
                text = if (tournament.status == TournamentStatus.LIVE) "Watch / Score 🔴" else "Join Match ⚔️",
                onClick = onJoin,
                modifier = Modifier.weight(1.2f),
                containerColor = if (tournament.status == TournamentStatus.LIVE) CyberRed else NeonGreen,
                glowColor = if (tournament.status == TournamentStatus.LIVE) CyberRed else NeonGreen
            )
        }
    }
}
