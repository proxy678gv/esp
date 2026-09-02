package com.example.payment

enum class PaymentTransactionType {
    TOURNAMENT_ENTRY_FEE,
    WALLET_DEPOSIT,
    PRIZE_PAYOUT,
    WITHDRAWAL
}

enum class PaymentGatewayStatus {
    INITIATED,
    PROCESSING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED
}

data class RazorpayPaymentPayload(
    val key: String,
    val amountPaise: Long,
    val currency: String = "INR",
    val name: String = "PG E-SPORTS 2.0",
    val description: String,
    val prefillEmail: String,
    val prefillContact: String,
    val themeColor: String = "#00FF66",
    val notes: Map<String, String> = emptyMap()
)

data class PaymentResultData(
    val isSuccess: Boolean,
    val paymentId: String?,
    val orderId: String? = null,
    val signature: String? = null,
    val errorCode: Int? = null,
    val errorMessage: String? = null,
    val amount: Double,
    val currency: String = "INR",
    val type: PaymentTransactionType = PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
    val tournamentId: String? = null,
    val tournamentTitle: String? = null,
    val teamId: String? = null,
    val teamName: String? = null,
    val paymentMethod: String = "UPI / Razorpay",
    val timestamp: Long = System.currentTimeMillis()
)
