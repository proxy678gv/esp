package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PGEsportsViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(
    viewModel: PGEsportsViewModel
) {
    val user by viewModel.currentUser.collectAsState()
    val transactions by viewModel.walletTransactions.collectAsState()

    var showTopUpDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var topUpAmountInput by remember { mutableStateOf("500") }
    var withdrawAmountInput by remember { mutableStateOf("1000") }
    var upiIdInput by remember { mutableStateOf("arun@oksbi") }

    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PG Wallet Main Card
        item {
            PGGlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonGreen,
                backgroundColor = DarkCardBg
            ) {
                Text("PG ESPORTS SECURE WALLET", fontSize = 11.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "₹${user?.walletBalance?.toInt() ?: 0}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonGreen
                        )
                        Text("Available for tournament entries & withdrawal", color = TextSecondary, fontSize = 11.sp)
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("TOTAL EARNED", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                        Text("₹${user?.totalWinnings?.toInt() ?: 0}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = CyberGold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PGNeonButton(
                        text = "+ Add Money",
                        onClick = { showTopUpDialog = true },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AccountBalanceWallet
                    )

                    PGSecondaryButton(
                        text = "Withdraw UPI",
                        onClick = { showWithdrawDialog = true },
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.ArrowOutward
                    )
                }
            }
        }

        // Immutable Ledger Title
        item {
            Text(
                text = "TRANSACTION HISTORY & AUDIT LEDGER",
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = TextMuted,
                letterSpacing = 1.sp
            )
        }

        if (transactions.isEmpty()) {
            item {
                Text("No transactions recorded yet.", color = TextSecondary, fontSize = 13.sp)
            }
        } else {
            items(transactions) { txn ->
                val isCredit = txn.amount > 0
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = DarkSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (isCredit) NeonGreen.copy(alpha = 0.2f) else CyberRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = if (isCredit) NeonGreen else CyberRed
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(txn.description, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 13.sp)
                                Text("Ref: ${txn.providerReference} • ${dateFormat.format(Date(txn.timestamp))}", color = TextSecondary, fontSize = 11.sp)
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (isCredit) "+" else ""}₹${txn.amount.toInt()}",
                                color = if (isCredit) NeonGreen else CyberRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Text(txn.status, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    var showRazorpayCheckout by remember { mutableStateOf(false) }
    var razorpayAmountToPay by remember { mutableStateOf(500.0) }

    // Top Up Amount Picker Dialog
    if (showTopUpDialog) {
        AlertDialog(
            onDismissRequest = { showTopUpDialog = false },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFF3395FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("R", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Money via Razorpay", fontWeight = FontWeight.Black, color = TextPrimary, fontFamily = SFProFontFamily)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Select quick deposit amount:", fontSize = 12.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("100", "250", "500", "1000", "2000").forEach { amt ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { topUpAmountInput = amt },
                                shape = RoundedCornerShape(8.dp),
                                color = if (topUpAmountInput == amt) NeonGreen else DarkCardBg,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (topUpAmountInput == amt) NeonGreen else DarkCardBorder)
                            ) {
                                Text(
                                    text = "₹$amt",
                                    color = if (topUpAmountInput == amt) DarkBackground else TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = topUpAmountInput,
                        onValueChange = { topUpAmountInput = it },
                        label = { Text("Custom Amount (₹)", fontFamily = SFProFontFamily) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        color = Color(0xFF0C2340),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3395FF).copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF3395FF), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Instant UPI, GPay, PhonePe, Paytm, Cards & NetBanking via Razorpay",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                fontFamily = SFProFontFamily
                            )
                        }
                    }
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Proceed to Razorpay 💳",
                    onClick = {
                        val amt = topUpAmountInput.toDoubleOrNull() ?: 500.0
                        razorpayAmountToPay = amt
                        showTopUpDialog = false
                        showRazorpayCheckout = true
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showTopUpDialog = false }) { Text("Cancel", color = TextSecondary, fontFamily = SFProFontFamily) }
            }
        )
    }

    // Active Razorpay Checkout Gateway Sheet
    if (showRazorpayCheckout) {
        RazorpayCheckoutDialog(
            amount = razorpayAmountToPay,
            description = "PG Wallet Deposit",
            customerEmail = user?.email ?: "player@pgesports.com",
            customerPhone = "+91 98765 43210",
            onDismiss = { showRazorpayCheckout = false },
            onPaymentSuccess = { result ->
                viewModel.addDeposit(result.amount)
                showRazorpayCheckout = false
            }
        )
    }

    // Withdraw Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = DarkSurface,
            title = { Text("Instant UPI Bank Withdrawal", fontWeight = FontWeight.Black, color = TextPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = withdrawAmountInput,
                        onValueChange = { withdrawAmountInput = it },
                        label = { Text("Withdrawal Amount (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = upiIdInput,
                        onValueChange = { upiIdInput = it },
                        label = { Text("Your UPI ID (VPA)") },
                        placeholder = { Text("username@okhdfcbank") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("⚡ Payouts are credited via IMPS within 15-30 mins after automated compliance check.", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                PGNeonButton(
                    text = "Confirm Withdrawal",
                    onClick = {
                        val amt = withdrawAmountInput.toDoubleOrNull() ?: 100.0
                        viewModel.requestWithdrawal(amt, upiIdInput)
                        showWithdrawDialog = false
                    }
                )
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) { Text("Cancel", color = TextSecondary) }
            }
        )
    }
}
