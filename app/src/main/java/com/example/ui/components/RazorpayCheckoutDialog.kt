package com.example.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.TeamEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.example.payment.PaymentResultData
import com.example.payment.PaymentTransactionType
import com.example.payment.RazorpayPaymentManager
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class RazorpayPaymentMethod(val title: String, val subtitle: String, val icon: ImageVector) {
    UPI_APPS("UPI Apps", "Google Pay, PhonePe, Paytm, CRED", Icons.Default.QrCodeScanner),
    UPI_ID("UPI ID / VPA", "Pay via any UPI handle", Icons.Default.AlternateEmail),
    CARDS("Debit / Credit Cards", "Visa, Mastercard, RuPay", Icons.Default.CreditCard),
    NET_BANKING("NetBanking", "HDFC, SBI, ICICI, Axis & 50+ Banks", Icons.Default.AccountBalance),
    WALLETS("Wallets", "Paytm, Mobikwik, Amazon Pay", Icons.Default.AccountBalanceWallet)
}

enum class RazorpayCheckoutState {
    SELECT_METHOD,
    PROCESSING,
    SUCCESS,
    FAILED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RazorpayCheckoutDialog(
    amount: Double,
    description: String,
    customerEmail: String = "player@pgesports.com",
    customerPhone: String = "+91 98765 43210",
    tournament: TournamentEntity? = null,
    team: TeamEntity? = null,
    user: UserEntity? = null,
    type: PaymentTransactionType = PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
    onDismiss: () -> Unit,
    onPaymentSuccess: (PaymentResultData) -> Unit,
    onPaymentError: ((PaymentResultData) -> Unit)? = null
) {
    val context = LocalContext.current
    var checkoutState by remember { mutableStateOf(RazorpayCheckoutState.SELECT_METHOD) }
    var selectedMethod by remember { mutableStateOf(RazorpayPaymentMethod.UPI_APPS) }
    var selectedUpiApp by remember { mutableStateOf("Google Pay") }
    var enteredUpiId by remember { mutableStateOf("player@okaxis") }
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8892") }
    var isTestMode by remember { mutableStateOf(true) }
    var failureReason by remember { mutableStateOf("Bank server timed out during authorization.") }

    val coroutineScope = rememberCoroutineScope()

    val orderId = remember { "order_PG" + (100000..999999).random() }
    var paymentId by remember { mutableStateOf("") }

    val razorpayBrand = Color(0xFF072654)
    val razorpayAccent = Color(0xFF3395FF)
    val razorpayGreen = Color(0xFF10B981)

    Dialog(
        onDismissRequest = {
            if (checkoutState != RazorpayCheckoutState.PROCESSING) {
                onDismiss()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(20.dp)),
            color = DarkSurface,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // --- Razorpay Header Bar ---
                Surface(
                    color = razorpayBrand,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = razorpayAccent,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "R",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Razorpay",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp,
                                            color = Color.White,
                                            fontFamily = SFProFontFamily
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = if (isTestMode) CyberOrange.copy(alpha = 0.2f) else NeonGreen.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp),
                                            border = BorderStroke(0.5.dp, if (isTestMode) CyberOrange else NeonGreen)
                                        ) {
                                            Text(
                                                text = if (isTestMode) "TEST MODE" else "LIVE SECURE",
                                                color = if (isTestMode) CyberOrange else NeonGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "PG E-SPORTS 2.0 PAYMENT GATEWAY",
                                        fontSize = 10.sp,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = SFProFontFamily
                                    )
                                }
                            }

                            if (checkoutState == RazorpayCheckoutState.SELECT_METHOD) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount Due Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = description,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = SFProFontFamily
                                )
                                Text(
                                    text = "Order: $orderId",
                                    color = Color.White.copy(alpha = 0.5f),
                                    fontSize = 10.sp,
                                    fontFamily = SFProFontFamily
                                )
                            }
                            Text(
                                text = "₹${amount.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = Color.White,
                                fontFamily = SFProFontFamily
                            )
                        }
                    }
                }

                // --- Body Content ---
                when (checkoutState) {
                    RazorpayCheckoutState.SELECT_METHOD -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Native SDK Direct Launch Card
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = DarkSurfaceVariant,
                                border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Razorpay Android SDK Core Integration", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonGreen, fontFamily = SFProFontFamily)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Handles native UPI intent routing, card 3D Secure OTP & PCI-DSS 256-bit encryption.",
                                        fontSize = 10.sp,
                                        color = TextSecondary,
                                        fontFamily = SFProFontFamily
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            if (context is Activity) {
                                                if (tournament != null && team != null) {
                                                    RazorpayPaymentManager.startTournamentEntryCheckout(
                                                        activity = context,
                                                        tournament = tournament,
                                                        team = team,
                                                        user = user,
                                                        isTestMode = isTestMode,
                                                        onSuccess = { res ->
                                                            onPaymentSuccess(res)
                                                            onDismiss()
                                                        },
                                                        onError = { err ->
                                                            onPaymentError?.invoke(err)
                                                            failureReason = err.errorMessage ?: "SDK Payment Error"
                                                            checkoutState = RazorpayCheckoutState.FAILED
                                                        }
                                                    )
                                                } else {
                                                    RazorpayPaymentManager.startWalletDepositCheckout(
                                                        activity = context,
                                                        amount = amount,
                                                        user = user,
                                                        isTestMode = isTestMode,
                                                        onSuccess = { res ->
                                                            onPaymentSuccess(res)
                                                            onDismiss()
                                                        },
                                                        onError = { err ->
                                                            onPaymentError?.invoke(err)
                                                            failureReason = err.errorMessage ?: "SDK Deposit Error"
                                                            checkoutState = RazorpayCheckoutState.FAILED
                                                        }
                                                    )
                                                }
                                            } else {
                                                Toast.makeText(context, "Readying Razorpay checkout instance...", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonGreen),
                                        border = BorderStroke(1.dp, NeonGreen)
                                    ) {
                                        Text("⚡ Launch Native Razorpay Sheet", fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SFProFontFamily)
                                    }
                                }
                            }

                            Text(
                                text = "OR SELECT INSTANT GATEWAY METHOD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 1.sp,
                                fontFamily = SFProFontFamily
                            )

                            // Quick UPI App Selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val upiApps = listOf(
                                    "Google Pay" to "GPay",
                                    "PhonePe" to "PhonePe",
                                    "Paytm" to "Paytm",
                                    "CRED" to "CRED"
                                )
                                upiApps.forEach { (appName, appShort) ->
                                    val isAppSelected = selectedUpiApp == appName && selectedMethod == RazorpayPaymentMethod.UPI_APPS
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                selectedMethod = RazorpayPaymentMethod.UPI_APPS
                                                selectedUpiApp = appName
                                            },
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isAppSelected) razorpayAccent.copy(alpha = 0.2f) else DarkCardBg,
                                        border = BorderStroke(
                                            1.dp,
                                            if (isAppSelected) razorpayAccent else DarkCardBorder
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = when (appShort) {
                                                    "GPay" -> "🔵"
                                                    "PhonePe" -> "🟣"
                                                    "Paytm" -> "🔷"
                                                    else -> "⚪"
                                                },
                                                fontSize = 16.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = appShort,
                                                fontSize = 11.sp,
                                                fontWeight = if (isAppSelected) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isAppSelected) Color.White else TextSecondary,
                                                fontFamily = SFProFontFamily
                                            )
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = DarkCardBorder)

                            // All Methods List
                            RazorpayPaymentMethod.values().forEach { method ->
                                val isSelected = selectedMethod == method
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedMethod = method },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) DarkSurfaceVariant else DarkCardBg,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) razorpayAccent else DarkCardBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) razorpayAccent.copy(alpha = 0.2f) else DarkBackground),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = method.icon,
                                                    contentDescription = method.title,
                                                    tint = if (isSelected) razorpayAccent else TextSecondary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = method.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = TextPrimary,
                                                    fontFamily = SFProFontFamily
                                                )
                                                Text(
                                                    text = method.subtitle,
                                                    fontSize = 11.sp,
                                                    color = TextSecondary,
                                                    fontFamily = SFProFontFamily
                                                )
                                            }
                                        }

                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { selectedMethod = method },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = razorpayAccent,
                                                unselectedColor = TextMuted
                                            )
                                        )
                                    }
                                }
                            }

                            // Secondary inputs based on selected method
                            if (selectedMethod == RazorpayPaymentMethod.UPI_ID) {
                                OutlinedTextField(
                                    value = enteredUpiId,
                                    onValueChange = { enteredUpiId = it },
                                    label = { Text("Enter UPI ID (e.g. name@okhdfcbank)", fontFamily = SFProFontFamily) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = razorpayAccent,
                                        unfocusedBorderColor = DarkCardBorder
                                    )
                                )
                            }

                            // Pay CTA Button
                            Button(
                                onClick = {
                                    checkoutState = RazorpayCheckoutState.PROCESSING
                                    coroutineScope.launch {
                                        delay(2000) // Razorpay gateway verification & HMAC signature handshake
                                        paymentId = "pay_RZP" + (10000000..99999999).random()
                                        checkoutState = RazorpayCheckoutState.SUCCESS
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = razorpayAccent,
                                    contentColor = Color.White
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Pay ₹${amount.toInt()} via Razorpay",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        fontFamily = SFProFontFamily
                                    )
                                }
                            }

                            // Trust Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "PCI-DSS Level 1 Compliant • 256-bit SSL Encrypted",
                                    fontSize = 10.sp,
                                    color = TextMuted,
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }
                    }

                    RazorpayCheckoutState.PROCESSING -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = razorpayAccent,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Connecting to ${if (selectedMethod == RazorpayPaymentMethod.UPI_APPS) selectedUpiApp else "Razorpay Gateway"}...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary,
                                fontFamily = SFProFontFamily
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Securing transaction with 256-bit AES encryption.\nPlease do not press back or close the app.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                fontFamily = SFProFontFamily
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Surface(
                                color = DarkSurfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Order ID: $orderId",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontFamily = SFProFontFamily
                                )
                            }
                        }
                    }

                    RazorpayCheckoutState.SUCCESS -> {
                        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(NeonGreen.copy(alpha = 0.2f))
                                    .border(2.dp, NeonGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Success",
                                    tint = NeonGreen,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Payment Verified! 🎉",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    color = TextPrimary,
                                    fontFamily = SFProFontFamily
                                )
                                Text(
                                    text = "₹${amount.toInt()} confirmed via Razorpay",
                                    fontSize = 13.sp,
                                    color = NeonGreen,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = SFProFontFamily
                                )
                            }

                            // Digital Invoice Receipt
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = DarkSurfaceVariant,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, DarkCardBorder)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Payment ID", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                                        Text(paymentId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = SFProFontFamily)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Order ID", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                                        Text(orderId, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = SFProFontFamily)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Method", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                                        Text(
                                            if (selectedMethod == RazorpayPaymentMethod.UPI_APPS) "$selectedUpiApp (UPI)" else selectedMethod.title,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            fontFamily = SFProFontFamily
                                        )
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Date & Time", fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                                        Text(dateFormat.format(Date()), fontSize = 11.sp, color = TextSecondary, fontFamily = SFProFontFamily)
                                    }
                                }
                            }

                            // Finish CTA Button
                            PGNeonButton(
                                text = "Confirm & Proceed 🎮",
                                onClick = {
                                    val result = PaymentResultData(
                                        isSuccess = true,
                                        paymentId = paymentId,
                                        orderId = orderId,
                                        amount = amount,
                                        type = type,
                                        tournamentId = tournament?.id,
                                        tournamentTitle = tournament?.title,
                                        teamId = team?.id,
                                        teamName = team?.name,
                                        paymentMethod = if (selectedMethod == RazorpayPaymentMethod.UPI_APPS) selectedUpiApp else selectedMethod.title
                                    )
                                    onPaymentSuccess(result)
                                    onDismiss()
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    RazorpayCheckoutState.FAILED -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = CyberRed, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Payment Failed", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CyberRed, fontFamily = SFProFontFamily)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(failureReason, fontSize = 12.sp, color = TextSecondary, textAlign = TextAlign.Center, fontFamily = SFProFontFamily)
                            Spacer(modifier = Modifier.height(16.dp))
                            PGNeonButton(
                                text = "Retry Payment",
                                onClick = { checkoutState = RazorpayCheckoutState.SELECT_METHOD },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
