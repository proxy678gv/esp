package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel

enum class FFTab(val title: String, val icon: ImageVector) {
    CHARACTERS("Characters", Icons.Default.Person),
    WEAPONS("Armory", Icons.Default.SportsEsports),
    PETS("Tactical Pets", Icons.Default.Pets),
    DIAMONDS("Diamond Vault", Icons.Default.Diamond),
    COMBO_SIM("Combo Builder", Icons.Default.AutoFixHigh)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeFireHubScreen(
    viewModel: PGEsportsViewModel,
    onNavigateToTournaments: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(FFTab.CHARACTERS) }
    val user by viewModel.currentUser.collectAsState()

    // Dialog & Detail states
    var selectedCharacter by remember { mutableStateOf<FFCharacter?>(null) }
    var selectedWeapon by remember { mutableStateOf<FFWeapon?>(null) }
    var selectedPet by remember { mutableStateOf<FFPet?>(null) }
    var showRedeemVoucherDialog by remember { mutableStateOf(false) }
    var generatedVoucherCode by remember { mutableStateOf("") }
    var selectedDiamondPack by remember { mutableStateOf<FFDiamondPack?>(null) }
    var showRazorpayDiamondCheckout by remember { mutableStateOf(false) }
    var razorpayPaymentInfo by remember { mutableStateOf<RazorpayPaymentResult?>(null) }

    // Weapon Compare State
    var showWeaponCompareModal by remember { mutableStateOf(false) }
    var compareWeapon1 by remember { mutableStateOf(FreeFireDatabase.weapons[0]) } // MP40
    var compareWeapon2 by remember { mutableStateOf(FreeFireDatabase.weapons[4]) } // M1887

    Scaffold(
        topBar = {
            Surface(
                color = DarkBackground,
                border = BorderStroke(0.5.dp, DarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "FREE FIRE",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 17.sp,
                                    color = CyberGold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = CyberOrange.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "BOOYAH HUB",
                                        color = CyberOrange,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Meta Database • 4K Armory • Diamonds Vault",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // Diamond Balance Pill
                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, CyberGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Diamond, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "💎 1,420",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
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
                .background(DarkBackground)
                .padding(padding)
        ) {
            // --- Tab Selector Bar ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FFTab.values()) { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { selectedTab = tab },
                        color = if (isSelected) CyberOrange else DarkSurfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) CyberGold else DarkCardBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                tint = if (isSelected) DarkBackground else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // --- Tab Content ---
            when (selectedTab) {
                FFTab.CHARACTERS -> FFCharactersTab(
                    onSelectCharacter = { selectedCharacter = it }
                )
                FFTab.WEAPONS -> FFWeaponsTab(
                    onSelectWeapon = { selectedWeapon = it },
                    onOpenCompare = { showWeaponCompareModal = true }
                )
                FFTab.PETS -> FFPetsTab(
                    onSelectPet = { selectedPet = it }
                )
                FFTab.DIAMONDS -> FFDiamondsTab(
                    walletBalance = user?.walletBalance ?: 0.0,
                    onBuyPack = { pack ->
                        selectedDiamondPack = pack
                        showRazorpayDiamondCheckout = true
                    }
                )
                FFTab.COMBO_SIM -> FFComboBuilderTab()
            }
        }
    }

    // --- Character Detail Bottom Sheet / Dialog ---
    selectedCharacter?.let { char ->
        FFCharacterDetailModal(
            character = char,
            onDismiss = { selectedCharacter = null }
        )
    }

    // --- Weapon Detail Dialog ---
    selectedWeapon?.let { weapon ->
        FFWeaponDetailModal(
            weapon = weapon,
            onDismiss = { selectedWeapon = null },
            onCompare = {
                compareWeapon1 = weapon
                selectedWeapon = null
                showWeaponCompareModal = true
            }
        )
    }

    // --- Pet Detail Dialog ---
    selectedPet?.let { pet ->
        FFPetDetailModal(
            pet = pet,
            onDismiss = { selectedPet = null }
        )
    }

    // --- Weapon Compare Modal ---
    if (showWeaponCompareModal) {
        FFWeaponCompareDialog(
            weapon1 = compareWeapon1,
            weapon2 = compareWeapon2,
            allWeapons = FreeFireDatabase.weapons,
            onSelectW1 = { compareWeapon1 = it },
            onSelectW2 = { compareWeapon2 = it },
            onDismiss = { showWeaponCompareModal = false }
        )
    }

    // --- Razorpay Diamond Checkout Gateway ---
    if (showRazorpayDiamondCheckout && selectedDiamondPack != null) {
        val pack = selectedDiamondPack!!
        RazorpayCheckoutDialog(
            amount = pack.priceInr,
            description = "Free Fire Diamond Top-Up: ${pack.diamonds} + ${pack.bonusDiamonds} 💎",
            customerEmail = user?.email ?: "gamer@pgesports.com",
            onDismiss = { showRazorpayDiamondCheckout = false },
            onPaymentSuccess = { result ->
                razorpayPaymentInfo = result
                val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
                val code = "FF-PG-" + (1..12).map { chars.random() }.joinToString("")
                generatedVoucherCode = code
                showRazorpayDiamondCheckout = false
                showRedeemVoucherDialog = true
            }
        )
    }

    // --- Diamond Voucher Generated Dialog ---
    if (showRedeemVoucherDialog && selectedDiamondPack != null) {
        val pack = selectedDiamondPack!!
        AlertDialog(
            onDismissRequest = { showRedeemVoucherDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Diamond Top-Up Voucher Ready! 💎", fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 16.sp, fontFamily = SFProFontFamily)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Pack: ${pack.diamonds} + ${pack.bonusDiamonds} Bonus Diamonds",
                        fontWeight = FontWeight.Bold,
                        color = CyberGold,
                        fontFamily = SFProFontFamily
                    )
                    Text(
                        text = "Amount Paid: ₹${pack.priceInr.toInt()} via Razorpay (${razorpayPaymentInfo?.paymentId ?: "pay_RZP9812"})",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontFamily = SFProFontFamily
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ElectricBlue)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("GARENA REDEEM CODE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = generatedVoucherCode,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = NeonGreen,
                                    letterSpacing = 1.sp
                                )
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("FF Voucher", generatedVoucherCode))
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonGreen)
                                }
                            }
                        }
                    }

                    Text(
                        text = "📌 How to Redeem: Open reward.ff.garena.com, log in to your Free Fire ID, and paste this 12-character code to receive diamonds in your in-game vault within 10 minutes.",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Copy & Done",
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("FF Voucher", generatedVoucherCode))
                        showRedeemVoucherDialog = false
                    }
                )
            }
        )
    }
}

// -------------------------------------------------------------
// 1. CHARACTERS TAB
// -------------------------------------------------------------
@Composable
fun FFCharactersTab(
    onSelectCharacter: (FFCharacter) -> Unit
) {
    var selectedSkillFilter by remember { mutableStateOf("ALL") }
    val characters = remember(selectedSkillFilter) {
        if (selectedSkillFilter == "ALL") FreeFireDatabase.characters
        else FreeFireDatabase.characters.filter { it.skillType == selectedSkillFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero 4K Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ff_chars_showcase),
                        contentDescription = "Free Fire Characters 4K",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, DarkBackground.copy(alpha = 0.9f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            color = CyberOrange,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "META SQUAD LEGENDS",
                                color = DarkBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Free Fire Pro Roster & Ability Combos",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Tournament tested active and passive skill synergies",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Skill Filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All (${FreeFireDatabase.characters.size})",
                    "ACTIVE" to "Active Skills ⚡",
                    "PASSIVE" to "Passive Skills 🛡️"
                )
                filters.forEach { (key, label) ->
                    val isSelected = selectedSkillFilter == key
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedSkillFilter = key },
                        color = if (isSelected) NeonGreen else DarkSurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) NeonGreen else DarkCardBorder)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) DarkBackground else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Character List
        items(characters) { char ->
            PGGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectCharacter(char) },
                borderColor = if (char.skillType == "ACTIVE") NeonGreen else ElectricBlue,
                backgroundColor = DarkCardBg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Avatar Initial Box
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        if (char.skillType == "ACTIVE")
                                            listOf(NeonGreen.copy(alpha = 0.3f), NeonPurple.copy(alpha = 0.3f))
                                        else
                                            listOf(ElectricBlue.copy(alpha = 0.3f), DarkSurfaceVariant)
                                    )
                                )
                                .border(
                                    1.dp,
                                    if (char.skillType == "ACTIVE") NeonGreen else ElectricBlue,
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = char.name.take(2).uppercase(),
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = if (char.skillType == "ACTIVE") NeonGreen else ElectricBlue
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = char.name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = if (char.skillType == "ACTIVE") NeonGreen.copy(alpha = 0.15f) else ElectricBlue.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = char.skillType,
                                        color = if (char.skillType == "ACTIVE") NeonGreen else ElectricBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Role: ${char.role}",
                                fontSize = 12.sp,
                                color = CyberOrange,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Skill: ${char.skillName}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Surface(
                            color = CyberGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${char.synergyScore}% SYNERGY",
                                color = CyberGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = "View", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = DarkCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = char.skillDescription,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 15.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. WEAPONS TAB
// -------------------------------------------------------------
@Composable
fun FFWeaponsTab(
    onSelectWeapon: (FFWeapon) -> Unit,
    onOpenCompare: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("ALL") }
    val weapons = remember(selectedCategory) {
        if (selectedCategory == "ALL") FreeFireDatabase.weapons
        else FreeFireDatabase.weapons.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Weapons 4K Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ff_weapons_evo),
                        contentDescription = "Free Fire Evo Weapons 4K",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, DarkBackground.copy(alpha = 0.9f))
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Surface(
                                color = CyberGold,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "EVO ARMORY 4K",
                                    color = DarkBackground,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Free Fire Tournament Weapons",
                                color = TextPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Full damage, rate of fire, & armor shredding specs",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        PGNeonButton(
                            text = "Compare ⚔️",
                            onClick = onOpenCompare,
                            containerColor = ElectricBlue,
                            glowColor = ElectricBlue,
                            contentColor = DarkBackground
                        )
                    }
                }
            }
        }

        // Category Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val cats = listOf(
                    "ALL" to "All Guns",
                    "SMG" to "SMGs",
                    "SHOTGUN" to "Shotguns",
                    "ASSAULT_RIFLE" to "ARs",
                    "MARKSMAN" to "Marksman",
                    "SNIPER" to "Snipers"
                )
                items(cats) { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedCategory = catKey },
                        color = if (isSelected) CyberOrange else DarkSurfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) CyberGold else DarkCardBorder)
                    ) {
                        Text(
                            text = catLabel,
                            color = if (isSelected) DarkBackground else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Weapon List
        items(weapons) { weapon ->
            PGGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectWeapon(weapon) },
                borderColor = when (weapon.tier) {
                    "S+" -> CyberGold
                    "S" -> NeonGreen
                    else -> ElectricBlue
                },
                backgroundColor = DarkCardBg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = weapon.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = CyberGold.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "TIER ${weapon.tier}",
                                    color = CyberGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        weapon.evoName?.let { evo ->
                            Text(
                                text = "🔥 $evo",
                                fontSize = 12.sp,
                                color = CyberOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = weapon.category.replace("_", " "),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Surface(
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, DarkCardBorder)
                    ) {
                        Text(
                            text = "Tap to Inspect 🔍",
                            fontSize = 10.sp,
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stat Bars
                FFStatBar(label = "Damage", value = weapon.damage, maxValue = 100, color = CyberRed)
                Spacer(modifier = Modifier.height(4.dp))
                FFStatBar(label = "Rate of Fire", value = weapon.rateOfFire, maxValue = 100, color = CyberOrange)
                Spacer(modifier = Modifier.height(4.dp))
                FFStatBar(label = "Armor Penetration", value = weapon.armorPenetration, maxValue = 100, color = NeonGreen)
            }
        }
    }
}

// -------------------------------------------------------------
// 3. PETS TAB
// -------------------------------------------------------------
@Composable
fun FFPetsTab(
    onSelectPet: (FFPet) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonPurple,
                backgroundColor = DarkSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Pets, contentDescription = null, tint = NeonPurpleLight, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "TACTICAL ESPORTS PETS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPurpleLight
                        )
                        Text(
                            text = "Game-changing Gloo Wall & utility generators",
                            fontSize = 13.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        items(FreeFireDatabase.pets) { pet ->
            PGGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectPet(pet) },
                borderColor = if (pet.tier == "S+") CyberGold else ElectricBlue,
                backgroundColor = DarkCardBg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberOrange.copy(alpha = 0.15f))
                                .border(1.dp, CyberOrange, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🐾",
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = pet.name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = CyberGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = pet.tier,
                                        color = CyberGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${pet.species} • Skill: ${pet.skillName}",
                                fontSize = 12.sp,
                                color = NeonGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Divider(color = DarkCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pet.skillDescription,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "💡 Tactical Tip: ${pet.tacticalValue}",
                        fontSize = 11.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. DIAMONDS VAULT & TOP-UP STORE TAB
// -------------------------------------------------------------
@Composable
fun FFDiamondsTab(
    walletBalance: Double,
    onBuyPack: (FFDiamondPack) -> Unit
) {
    var evoLevel by remember { mutableStateOf(1) }
    var evoGunIndex by remember { mutableStateOf(0) }
    val currentEvoGun = FreeFireDatabase.evoGuns[evoGunIndex]

    val diamondsNeededForLevel = remember(evoLevel) {
        when (evoLevel) {
            1 -> 0
            2 -> 600
            3 -> 1200
            4 -> 2400
            5 -> 4000
            6 -> 8000
            else -> 14500
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Diamonds 4K Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_ff_diamond_vault),
                        contentDescription = "Free Fire Diamond Vault 4K",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, DarkBackground.copy(alpha = 0.9f))
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Surface(
                            color = ElectricBlue,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "OFFICIAL DIAMONDS VAULT",
                                color = DarkBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Instant Voucher Codes & Evo Calculators",
                            color = TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Convert tournament PG Wallet winnings directly to FF Diamonds",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Wallet Balance Conversion Banner
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonGreen,
                backgroundColor = DarkSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("YOUR PG WALLET BALANCE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("₹${walletBalance.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                    }

                    Surface(
                        color = NeonGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "≈ ${(walletBalance * 1.35).toInt()} 💎 FREE FIRE",
                            color = NeonGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Diamond Packs Grid
        item {
            Text(
                text = "TOP-UP DIAMOND PACKS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FreeFireDatabase.diamondPacks.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { pack ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onBuyPack(pack) },
                                shape = RoundedCornerShape(14.dp),
                                color = DarkCardBg,
                                border = BorderStroke(
                                    1.dp,
                                    if (pack.isPopular) CyberGold else DarkCardBorder
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    if (pack.tag != null) {
                                        Surface(
                                            color = if (pack.isPopular) CyberGold else CyberOrange,
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = pack.tag,
                                                color = DarkBackground,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Diamond, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${pack.diamonds}",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 18.sp,
                                            color = TextPrimary
                                        )
                                    }

                                    Text(
                                        text = "+${pack.bonusDiamonds} Bonus 💎",
                                        color = NeonGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        color = DarkSurfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "₹${pack.priceInr.toInt()}",
                                            color = CyberGold,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Memberships Section
        item {
            Text(
                text = "SPECIAL MEMBERSHIPS & PASSES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        items(FreeFireDatabase.memberships) { mem ->
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberGold,
                backgroundColor = DarkCardBg
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Surface(
                            color = CyberGold,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = mem.badge,
                                color = DarkBackground,
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mem.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Total ${mem.totalDiamonds} 💎 • ${mem.durationDays} Days",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    PGNeonButton(
                        text = "₹${mem.priceInr.toInt()}",
                        onClick = {
                            val pack = FFDiamondPack(mem.id, mem.totalDiamonds, 0, mem.priceInr, mem.badge, false)
                            onBuyPack(pack)
                        },
                        containerColor = CyberGold,
                        glowColor = CyberGold,
                        contentColor = DarkBackground
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = DarkCardBorder)
                Spacer(modifier = Modifier.height(8.dp))

                mem.perks.forEach { perk ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(perk, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        // Evo Gun Diamond Calculator
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberOrange,
                backgroundColor = DarkSurface
            ) {
                Text(
                    text = "⚙️ EVO GUN TOKEN & DIAMOND CALCULATOR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = CyberOrange,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selector for Evo Gun
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FreeFireDatabase.evoGuns.forEachIndexed { index, gun ->
                        val isSelected = evoGunIndex == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { evoGunIndex = index },
                            color = if (isSelected) CyberOrange else DarkSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) CyberGold else DarkCardBorder)
                        ) {
                            Text(
                                text = gun.gunName,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Target Evolution: Level $evoLevel / 7 (${currentEvoGun.evoSkinName})",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 14.sp
                )

                Slider(
                    value = evoLevel.toFloat(),
                    onValueChange = { evoLevel = it.toInt() },
                    valueRange = 1f..7f,
                    steps = 5,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberGold,
                        activeTrackColor = CyberOrange,
                        inactiveTrackColor = DarkCardBorder
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("DIAMONDS REQUIRED", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("$diamondsNeededForLevel 💎", fontSize = 18.sp, fontWeight = FontWeight.Black, color = CyberGold)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("ESTIMATED INR COST", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("₹${(diamondsNeededForLevel * 0.77).toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. COMBO BUILDER TAB
// -------------------------------------------------------------
@Composable
fun FFComboBuilderTab() {
    val activeChars = remember { FreeFireDatabase.characters.filter { it.skillType == "ACTIVE" } }
    val passiveChars = remember { FreeFireDatabase.characters.filter { it.skillType == "PASSIVE" } }
    val pets = remember { FreeFireDatabase.pets }

    var selectedActive by remember { mutableStateOf(activeChars[0]) } // DJ Alok
    var selectedPassive1 by remember { mutableStateOf(passiveChars[0]) } // Kelly
    var selectedPassive2 by remember { mutableStateOf(passiveChars[1]) } // Hayato
    var selectedPassive3 by remember { mutableStateOf(passiveChars[2]) } // Moco
    var selectedPet by remember { mutableStateOf(pets[0]) } // Mr Waggor

    val synergyScore = remember(selectedActive, selectedPassive1, selectedPassive2, selectedPassive3, selectedPet) {
        val base = (selectedActive.synergyScore + selectedPassive1.synergyScore + selectedPassive2.synergyScore + selectedPassive3.synergyScore) / 4
        (base + 2).coerceAtMost(99)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonGreen,
                backgroundColor = DarkSurfaceVariant
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("SQUAD SYNERGY SCORE", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("$synergyScore / 100", fontSize = 24.sp, fontWeight = FontWeight.Black, color = NeonGreen)
                        Text(
                            text = if (synergyScore >= 95) "🔥 Meta Tournament Loadout" else "⚡ Balanced Ranked Setup",
                            color = CyberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = NeonGreen.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("S+", fontWeight = FontWeight.Black, fontSize = 20.sp, color = NeonGreen)
                        }
                    }
                }
            }
        }

        // Active Skill Slot
        item {
            Text("1. ACTIVE SKILL (LEADER)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(activeChars) { char ->
                    val isSelected = selectedActive.id == char.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedActive = char },
                        color = if (isSelected) NeonGreen else DarkCardBg,
                        border = BorderStroke(1.dp, if (isSelected) NeonGreen else DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(char.name, fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (isSelected) DarkBackground else TextPrimary)
                            Text(char.skillName, fontSize = 10.sp, color = if (isSelected) DarkBackground else TextSecondary)
                        }
                    }
                }
            }
        }

        // Passive 1
        item {
            Text("2. PASSIVE SKILL 1 (MOBILITY)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(passiveChars) { char ->
                    val isSelected = selectedPassive1.id == char.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPassive1 = char },
                        color = if (isSelected) ElectricBlue else DarkCardBg,
                        border = BorderStroke(1.dp, if (isSelected) ElectricBlue else DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(char.name, fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (isSelected) DarkBackground else TextPrimary)
                            Text(char.skillName, fontSize = 10.sp, color = if (isSelected) DarkBackground else TextSecondary)
                        }
                    }
                }
            }
        }

        // Passive 2
        item {
            Text("3. PASSIVE SKILL 2 (COMBAT / PENETRATION)", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(passiveChars) { char ->
                    val isSelected = selectedPassive2.id == char.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPassive2 = char },
                        color = if (isSelected) CyberOrange else DarkCardBg,
                        border = BorderStroke(1.dp, if (isSelected) CyberOrange else DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(char.name, fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (isSelected) DarkBackground else TextPrimary)
                            Text(char.skillName, fontSize = 10.sp, color = if (isSelected) DarkBackground else TextSecondary)
                        }
                    }
                }
            }
        }

        // Pet Slot
        item {
            Text("4. COMPANION TACTICAL PET", fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(pets) { pet ->
                    val isSelected = selectedPet.id == pet.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedPet = pet },
                        color = if (isSelected) CyberGold else DarkCardBg,
                        border = BorderStroke(1.dp, if (isSelected) CyberGold else DarkCardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(pet.name, fontWeight = FontWeight.Black, fontSize = 13.sp, color = if (isSelected) DarkBackground else TextPrimary)
                            Text(pet.skillName, fontSize = 10.sp, color = if (isSelected) DarkBackground else TextSecondary)
                        }
                    }
                }
            }
        }

        // Tactical Summary Card
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = CyberGold,
                backgroundColor = DarkCardBg
            ) {
                Text(
                    text = "🎯 AI ESPORTS COACH VERDICT",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = CyberGold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Your combo (${selectedActive.name} + ${selectedPassive1.name} + ${selectedPassive2.name} + ${selectedPet.name}) gives high aggressive push potential. ${selectedActive.name}'s ${selectedActive.skillName} lets you breach buildings, while ${selectedPet.name}'s ${selectedPet.skillName} ensures constant utility cover in late zones.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

// -------------------------------------------------------------
// HELPER COMPONENTS & MODALS
// -------------------------------------------------------------
@Composable
fun FFStatBar(label: String, value: Int, maxValue: Int, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontSize = 11.sp, color = TextSecondary)
            Text("$value", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = DarkSurfaceVariant
        )
    }
}

@Composable
fun FFCharacterDetailModal(
    character: FFCharacter,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(character.name, fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 20.sp)
                    Text(character.alias, color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = CyberGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${character.synergyScore}% METASCORE",
                        color = CyberGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = DarkSurfaceVariant,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("SKILL: ${character.skillName.uppercase()}", fontWeight = FontWeight.Black, fontSize = 12.sp, color = ElectricBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(character.skillDescription, fontSize = 12.sp, color = TextPrimary, lineHeight = 16.sp)
                    }
                }

                Text("TACTICAL TOURNAMENT TIPS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextMuted)
                Text(character.tacticalTips, fontSize = 12.sp, color = TextSecondary)

                Text("LORE & ORIGIN", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextMuted)
                Text(character.lore, fontSize = 11.sp, color = TextSecondary)

                Surface(
                    color = DarkBackground,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(character.voiceQuote, fontSize = 11.sp, color = TextPrimary, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                    }
                }
            }
        },
        confirmButton = {
            PGNeonButton(
                text = "Close",
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun FFWeaponDetailModal(
    weapon: FFWeapon,
    onDismiss: () -> Unit,
    onCompare: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(weapon.name, fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 20.sp)
                    weapon.evoName?.let {
                        Text(it, color = CyberOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Surface(
                    color = CyberGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("TIER ${weapon.tier}", color = CyberGold, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp))
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(weapon.description, fontSize = 12.sp, color = TextSecondary)

                Spacer(modifier = Modifier.height(6.dp))
                Text("WEAPON SPECIFICATIONS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextMuted)

                FFStatBar("Damage", weapon.damage, 100, CyberRed)
                FFStatBar("Rate of Fire", weapon.rateOfFire, 100, CyberOrange)
                FFStatBar("Range", weapon.range, 100, ElectricBlue)
                FFStatBar("Armor Penetration", weapon.armorPenetration, 100, NeonGreen)
                FFStatBar("Reload Speed", weapon.reloadSpeed, 100, CyberGold)
                FFStatBar("Accuracy", weapon.accuracy, 100, NeonPurpleLight)

                Spacer(modifier = Modifier.height(6.dp))
                Text("BEST ATTACHMENTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = TextMuted)
                Text(weapon.bestAttachments.joinToString(" • "), fontSize = 11.sp, color = TextPrimary)
            }
        },
        confirmButton = {
            PGNeonButton(
                text = "Compare Gun ⚔️",
                onClick = onCompare
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextSecondary)
            }
        }
    )
}

@Composable
fun FFPetDetailModal(
    pet: FFPet,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(pet.name, fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 20.sp)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Species: ${pet.species}", color = CyberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Skill: ${pet.skillName} (${pet.cooldown})", color = NeonGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(pet.skillDescription, fontSize = 12.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Best Pairing Characters:", fontWeight = FontWeight.Bold, color = TextMuted, fontSize = 11.sp)
                Text(pet.bestPairings.joinToString(", "), color = ElectricBlue, fontSize = 12.sp)
            }
        },
        confirmButton = {
            PGNeonButton(text = "Close", onClick = onDismiss)
        }
    )
}

@Composable
fun FFWeaponCompareDialog(
    weapon1: FFWeapon,
    weapon2: FFWeapon,
    allWeapons: List<FFWeapon>,
    onSelectW1: (FFWeapon) -> Unit,
    onSelectW2: (FFWeapon) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text("⚔️ Weapon Battle Comparator", fontWeight = FontWeight.Black, color = TextPrimary, fontSize = 18.sp)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Guns selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Weapon 1 (Left)", fontSize = 10.sp, color = CyberRed, fontWeight = FontWeight.Bold)
                        Text(weapon1.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                    }
                    Text("VS", fontWeight = FontWeight.Black, fontSize = 16.sp, color = CyberGold, modifier = Modifier.padding(horizontal = 8.dp))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("Weapon 2 (Right)", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                        Text(weapon2.name, fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                    }
                }

                Divider(color = DarkCardBorder)

                CompareStatRow("Damage", weapon1.damage, weapon2.damage)
                CompareStatRow("Rate of Fire", weapon1.rateOfFire, weapon2.rateOfFire)
                CompareStatRow("Range", weapon1.range, weapon2.range)
                CompareStatRow("Armor Penetration", weapon1.armorPenetration, weapon2.armorPenetration)
                CompareStatRow("Reload Speed", weapon1.reloadSpeed, weapon2.reloadSpeed)
                CompareStatRow("Accuracy", weapon1.accuracy, weapon2.accuracy)
            }
        },
        confirmButton = {
            PGNeonButton(text = "Done", onClick = onDismiss)
        }
    )
}

@Composable
fun CompareStatRow(statName: String, val1: Int, val2: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$val1", fontWeight = FontWeight.Black, color = if (val1 >= val2) CyberRed else TextSecondary, fontSize = 12.sp)
            Text(statName, fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
            Text("$val2", fontWeight = FontWeight.Black, color = if (val2 >= val1) ElectricBlue else TextSecondary, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LinearProgressIndicator(
                progress = { (val1.toFloat() / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = CyberRed,
                trackColor = DarkSurfaceVariant
            )
            LinearProgressIndicator(
                progress = { (val2.toFloat() / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ElectricBlue,
                trackColor = DarkSurfaceVariant
            )
        }
    }
}
