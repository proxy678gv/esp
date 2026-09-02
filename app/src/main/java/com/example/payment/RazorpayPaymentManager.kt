package com.example.payment

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.data.model.TeamEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.razorpay.Checkout
import com.razorpay.PaymentData
import org.json.JSONObject
import java.util.UUID

object RazorpayPaymentManager {

    private const val TAG = "RazorpayPaymentManager"
    const val DEFAULT_TEST_KEY = "rzp_test_517uX8PG9874"
    const val DEFAULT_LIVE_KEY = "rzp_live_517uX8PG9874"

    // Active pending payment request context
    private var pendingSuccessCallback: ((PaymentResultData) -> Unit)? = null
    private var pendingErrorCallback: ((PaymentResultData) -> Unit)? = null
    private var activeTransactionMeta: ActivePaymentMeta? = null

    data class ActivePaymentMeta(
        val type: PaymentTransactionType,
        val amount: Double,
        val tournamentId: String? = null,
        val tournamentTitle: String? = null,
        val teamId: String? = null,
        val teamName: String? = null,
        val userId: String? = null
    )

    fun preload(context: Context) {
        try {
            Checkout.preload(context.applicationContext)
            Log.d(TAG, "Razorpay Checkout preloaded successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to preload Razorpay Checkout: ${e.message}")
        }
    }

    /**
     * Start Razorpay Checkout for Tournament Entry Fee Payment
     */
    fun startTournamentEntryCheckout(
        activity: Activity,
        tournament: TournamentEntity,
        team: TeamEntity,
        user: UserEntity?,
        isTestMode: Boolean = true,
        onSuccess: (PaymentResultData) -> Unit,
        onError: (PaymentResultData) -> Unit
    ) {
        val amountInPaise = (tournament.entryFee * 100).toLong()
        val email = user?.email?.takeIf { it.isNotBlank() } ?: "player@pgesports.in"
        val mobile = user?.mobile?.takeIf { it.isNotBlank() } ?: "+919876543210"
        val key = if (isTestMode) DEFAULT_TEST_KEY else DEFAULT_LIVE_KEY

        activeTransactionMeta = ActivePaymentMeta(
            type = PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
            amount = tournament.entryFee,
            tournamentId = tournament.id,
            tournamentTitle = tournament.title,
            teamId = team.id,
            teamName = team.name,
            userId = user?.id
        )
        pendingSuccessCallback = onSuccess
        pendingErrorCallback = onError

        val checkout = Checkout()
        checkout.setKeyID(key)

        try {
            val options = JSONObject()
            options.put("name", "PG E-SPORTS 2.0")
            options.put("description", "Entry Fee: ${tournament.title}")
            options.put("currency", "INR")
            options.put("amount", amountInPaise)
            options.put("send_sms_hash", true)

            // Theme customization matching esports dark & neon green
            val theme = JSONObject()
            theme.put("color", "#00FF66")
            theme.put("backdrop_color", "#0B0E17")
            options.put("theme", theme)

            // Prefill user details
            val prefill = JSONObject()
            prefill.put("email", email)
            prefill.put("contact", mobile)
            options.put("prefill", prefill)

            // Custom metadata notes for backend transaction reconciliation
            val notes = JSONObject()
            notes.put("tournament_id", tournament.id)
            notes.put("tournament_title", tournament.title)
            notes.put("team_id", team.id)
            notes.put("team_name", team.name)
            notes.put("user_id", user?.id ?: "UNKNOWN")
            notes.put("platform", "PG_ESPORTS_ANDROID_2.0")
            options.put("notes", notes)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 3)
            options.put("retry", retryObj)

            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating Razorpay checkout: ${e.message}", e)
            val failureResult = PaymentResultData(
                isSuccess = false,
                paymentId = null,
                errorCode = -1,
                errorMessage = e.message ?: "Failed to open Razorpay Checkout Activity",
                amount = tournament.entryFee,
                type = PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
                tournamentId = tournament.id,
                tournamentTitle = tournament.title,
                teamId = team.id,
                teamName = team.name
            )
            onError(failureResult)
            clearCallbacks()
        }
    }

    /**
     * Start Razorpay Checkout for Wallet Deposit
     */
    fun startWalletDepositCheckout(
        activity: Activity,
        amount: Double,
        user: UserEntity?,
        isTestMode: Boolean = true,
        onSuccess: (PaymentResultData) -> Unit,
        onError: (PaymentResultData) -> Unit
    ) {
        val amountInPaise = (amount * 100).toLong()
        val email = user?.email?.takeIf { it.isNotBlank() } ?: "player@pgesports.in"
        val mobile = user?.mobile?.takeIf { it.isNotBlank() } ?: "+919876543210"
        val key = if (isTestMode) DEFAULT_TEST_KEY else DEFAULT_LIVE_KEY

        activeTransactionMeta = ActivePaymentMeta(
            type = PaymentTransactionType.WALLET_DEPOSIT,
            amount = amount,
            userId = user?.id
        )
        pendingSuccessCallback = onSuccess
        pendingErrorCallback = onError

        val checkout = Checkout()
        checkout.setKeyID(key)

        try {
            val options = JSONObject()
            options.put("name", "PG E-SPORTS 2.0")
            options.put("description", "PG Wallet Instant Deposit")
            options.put("currency", "INR")
            options.put("amount", amountInPaise)
            options.put("send_sms_hash", true)

            val theme = JSONObject()
            theme.put("color", "#00FF66")
            theme.put("backdrop_color", "#0B0E17")
            options.put("theme", theme)

            val prefill = JSONObject()
            prefill.put("email", email)
            prefill.put("contact", mobile)
            options.put("prefill", prefill)

            val notes = JSONObject()
            notes.put("type", "WALLET_DEPOSIT")
            notes.put("user_id", user?.id ?: "UNKNOWN")
            notes.put("platform", "PG_ESPORTS_ANDROID_2.0")
            options.put("notes", notes)

            checkout.open(activity, options)
        } catch (e: Exception) {
            Log.e(TAG, "Error initiating Razorpay wallet deposit: ${e.message}", e)
            val failureResult = PaymentResultData(
                isSuccess = false,
                paymentId = null,
                errorCode = -1,
                errorMessage = e.message ?: "Failed to open Razorpay Checkout Activity",
                amount = amount,
                type = PaymentTransactionType.WALLET_DEPOSIT
            )
            onError(failureResult)
            clearCallbacks()
        }
    }

    /**
     * Handlers called from MainActivity PaymentResultWithDataListener callbacks
     */
    fun handlePaymentSuccess(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Log.d(TAG, "Payment Success Callback: PaymentID=$razorpayPaymentId, OrderID=${paymentData?.orderId}")
        val meta = activeTransactionMeta
        val result = PaymentResultData(
            isSuccess = true,
            paymentId = razorpayPaymentId ?: paymentData?.paymentId ?: ("PAY_RZP_" + UUID.randomUUID().toString().take(8).uppercase()),
            orderId = paymentData?.orderId,
            signature = paymentData?.signature,
            amount = meta?.amount ?: 0.0,
            type = meta?.type ?: PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
            tournamentId = meta?.tournamentId,
            tournamentTitle = meta?.tournamentTitle,
            teamId = meta?.teamId,
            teamName = meta?.teamName
        )
        pendingSuccessCallback?.invoke(result)
        clearCallbacks()
    }

    fun handlePaymentError(errorCode: Int, errorDescription: String?, paymentData: PaymentData?) {
        Log.e(TAG, "Payment Error Callback: Code=$errorCode, Desc=$errorDescription, PaymentID=${paymentData?.paymentId}")
        val meta = activeTransactionMeta
        val readableError = when (errorCode) {
            Checkout.PAYMENT_CANCELED -> "Payment was cancelled by the user."
            Checkout.NETWORK_ERROR -> "Network error occurred during payment processing. Please check internet connection."
            Checkout.INVALID_OPTIONS -> "Invalid payment options configured. Please try again."
            Checkout.TLS_ERROR -> "Device does not support required TLS version for payment security."
            else -> errorDescription ?: "Payment transaction failed. Please retry."
        }

        val result = PaymentResultData(
            isSuccess = false,
            paymentId = paymentData?.paymentId,
            orderId = paymentData?.orderId,
            signature = paymentData?.signature,
            errorCode = errorCode,
            errorMessage = readableError,
            amount = meta?.amount ?: 0.0,
            type = meta?.type ?: PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
            tournamentId = meta?.tournamentId,
            tournamentTitle = meta?.tournamentTitle,
            teamId = meta?.teamId,
            teamName = meta?.teamName
        )
        pendingErrorCallback?.invoke(result)
        clearCallbacks()
    }

    private fun clearCallbacks() {
        pendingSuccessCallback = null
        pendingErrorCallback = null
        activeTransactionMeta = null
    }
}
