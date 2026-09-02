package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PGRankTier
import com.example.data.model.TournamentStatus
import com.example.ui.theme.*

// Glassmorphism Card Container
@Composable
fun PGGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderColor: Color = GlassBorderHighlight,
    backgroundColor: Color = GlassWhite10,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        borderColor.copy(alpha = 0.4f),
                        borderColor.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(cornerRadius), ambientColor = Color.Black, spotColor = NeonPurple.copy(alpha = 0.3f)),
        color = backgroundColor,
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content
        )
    }
}

// Neon Glow Primary Button
@Composable
fun PGNeonButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    glowColor: Color = NeonGreen,
    containerColor: Color = NeonGreen,
    contentColor: Color = DarkBackground
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .shadow(
                elevation = if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = glowColor,
                spotColor = glowColor
            ),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = DarkSurfaceVariant,
            disabledContentColor = TextMuted
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Secondary Outlined Glass Button
@Composable
fun PGSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    borderColor: Color = NeonPurple
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = TextPrimary,
            containerColor = DarkSurface.copy(alpha = 0.6f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = borderColor
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

// Tournament Status Badge
@Composable
fun PGStatusBadge(status: TournamentStatus) {
    val (bgColor, textColor, label) = when (status) {
        TournamentStatus.LIVE -> Triple(CyberRed.copy(alpha = 0.2f), CyberRed, "🔴 LIVE")
        TournamentStatus.REGISTRATION_OPEN -> Triple(NeonGreen.copy(alpha = 0.15f), NeonGreen, "🟢 REG OPEN")
        TournamentStatus.CHECK_IN -> Triple(CyberOrange.copy(alpha = 0.2f), CyberOrange, "⏱️ CHECK-IN")
        TournamentStatus.REGISTRATION_CLOSED -> Triple(TextMuted.copy(alpha = 0.2f), TextSecondary, "🔒 REG CLOSED")
        TournamentStatus.COMPLETED -> Triple(NeonPurple.copy(alpha = 0.2f), NeonPurpleLight, "🏆 COMPLETED")
        TournamentStatus.UNDER_REVIEW -> Triple(CyberGold.copy(alpha = 0.2f), CyberGold, "⚖️ UNDER REVIEW")
        TournamentStatus.PENDING_APPROVAL -> Triple(ElectricBlue.copy(alpha = 0.2f), ElectricBlue, "📋 PENDING APPROVAL")
        TournamentStatus.APPROVED -> Triple(NeonGreen.copy(alpha = 0.15f), NeonGreen, "✅ APPROVED")
        TournamentStatus.DRAFT -> Triple(DarkCardBorder, TextSecondary, "📝 DRAFT")
        TournamentStatus.RESULTS_PENDING -> Triple(CyberGold.copy(alpha = 0.2f), CyberGold, "📊 SCORING")
        TournamentStatus.CANCELLED -> Triple(CyberRed.copy(alpha = 0.2f), CyberRed, "❌ CANCELLED")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// PG Rank Tier Badge
@Composable
fun PGRankBadge(tier: PGRankTier, rating: Int? = null) {
    val badgeColor = Color(tier.badgeColor)
    Surface(
        color = badgeColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.6f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = badgeColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = tier.displayName.uppercase(),
                color = badgeColor,
                fontWeight = FontWeight.Black,
                fontSize = 11.sp
            )
            if (rating != null) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "($rating)",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Animated Live Pulsing Indicator
@Composable
fun PGLivePulseIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(CyberRed.copy(alpha = alpha))
    )
}
