package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PGRankTier
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

@Composable
fun PGRankScreen(
    viewModel: PGEsportsViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val usersByRank by viewModel.usersByRank.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Current Rating & Tier Hero Card
        item {
            user?.let { u ->
                val tierColor = Color(u.rankTier.badgeColor)
                PGGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = tierColor,
                    backgroundColor = DarkCardBg
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("MY PG RATING", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${u.rating} PTS",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = NeonGreen
                            )
                        }

                        PGRankBadge(tier = u.rankTier)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tier Progress Bar
                    val nextTierMin = when (u.rankTier) {
                        PGRankTier.BRONZE -> 1000
                        PGRankTier.SILVER -> 1500
                        PGRankTier.GOLD -> 2000
                        PGRankTier.PLATINUM -> 2600
                        PGRankTier.DIAMOND -> 3200
                        PGRankTier.MASTER -> 4000
                        PGRankTier.GRANDMASTER -> 5000
                        PGRankTier.LEGEND -> 6000
                    }
                    val progress = (u.rating.toFloat() / nextTierMin.toFloat()).coerceIn(0f, 1f)

                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Current: ${u.rankTier.name}", fontSize = 11.sp, color = TextSecondary)
                            Text("Next Tier at $nextTierMin PTS", fontSize = 11.sp, color = tierColor, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = tierColor,
                            trackColor = DarkSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = DarkCardBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Player Career Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(title = "Matches", value = "${u.totalMatches}")
                        StatItem(title = "Victories", value = "${u.totalWins}")
                        StatItem(title = "Total Kills", value = "${u.totalKills}")
                        StatItem(title = "K/D Ratio", value = "${u.kdRatio}")
                    }
                }
            }
        }

        // Tiers Reference Ladder
        item {
            Text(
                text = "PG RANK RATING TIERS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = DarkSurface
            ) {
                val tiers = PGRankTier.values()
                tiers.forEach { tier ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PGRankBadge(tier = tier)
                        Text(
                            text = "${tier.minRating}+ Rating",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Global Leaderboard
        item {
            Text(
                text = "GLOBAL ESPORTS LEADERBOARD",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        itemsIndexed(usersByRank) { index, player ->
            val rankPos = index + 1
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (player.id == user?.id) NeonPurple.copy(alpha = 0.15f) else DarkCardBg,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (player.id == user?.id) NeonPurple else DarkCardBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = when (rankPos) {
                                1 -> CyberGold.copy(alpha = 0.2f)
                                2 -> Color(0xFFC0C0C0).copy(alpha = 0.2f)
                                3 -> Color(0xFFCD7F32).copy(alpha = 0.2f)
                                else -> DarkSurfaceVariant
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "#$rankPos",
                                    color = when (rankPos) {
                                        1 -> CyberGold
                                        2 -> Color(0xFFC0C0C0)
                                        3 -> Color(0xFFCD7F32)
                                        else -> TextSecondary
                                    },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(player.username, fontWeight = FontWeight.Bold, color = TextPrimary)
                                if (player.id == user?.id) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("(You)", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("Win Rate: ${player.winRatePercent}% • ${player.totalWins} Wins", color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${player.rating} PTS",
                            color = NeonGreen,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                        PGRankBadge(tier = player.rankTier)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
    }
}
