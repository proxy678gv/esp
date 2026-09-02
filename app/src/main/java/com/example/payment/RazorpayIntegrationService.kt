package com.example.payment

import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.data.model.TeamEntity
import com.example.data.model.TournamentEntity
import com.example.data.model.UserEntity
import com.razorpay.Checkout
import com.razorpay.PaymentData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.UUID

/**
 * Listener interface for intercepting and handling Razorpay checkout lifecycle
 * and payment callbacks across the application flow.
 */
interface RazorpayCheckoutListener {
    /**
     * Triggered when a checkout transaction is prepared and sent to the Razorpay SDK.
     */
    fun onPaymentInitiated(orderId: String, payload: RazorpayPaymentPayload) {}

    /**
     * Triggered on successful transaction authorization by the bank/gateway.
     */
    fun onPaymentSuccess(result: PaymentResultData)

    /**
     * Triggered when payment fails due to bank errors, bad payload, or connectivity issues.
     */
    fun onPaymentError(error: PaymentResultData)

    /**
     * Triggered if the user dismisses or cancels the checkout bottom sheet.
     */
    fun onPaymentCancelled(reason: String) {}
}

/**
 * Observable UI state stream for reactive Compose integration.
 */
sealed interface PaymentFlowState {
    object Idle : PaymentFlowState
    data class Initializing(val isTestMode: Boolean) : PaymentFlowState
    data class Ready(val keyId: String) : PaymentFlowState
    data class Processing(val orderId: String, val amount: Double, val description: String) : PaymentFlowState
    data class Success(val result: PaymentResultData) : PaymentFlowState
    data class Failed(val error: PaymentResultData) : PaymentFlowState
    data class Cancelled(val message: String) : PaymentFlowState
}

/**
 * Dedicated Razorpay Integration Service that initializes the SDK, prepares
 * checkout options, registers listeners, and handles application payment callbacks.
 */
class RazorpayIntegrationService private constructor() {

    companion object {
        private const val TAG = "RazorpayService"

        const val DEFAULT_TEST_KEY = "rzp_test_517uX8PG9874"
        const val DEFAULT_LIVE_KEY = "rzp_live_517uX8PG9874"

        @Volatile
        private var instance: RazorpayIntegrationService? = null

        fun getInstance(): RazorpayIntegrationService {
            return instance ?: synchronized(this) {
                instance ?: RazorpayIntegrationService().also { instance = it }
            }
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // Internal State
    private var isInitialized = false
    private var activeKeyId: String = DEFAULT_TEST_KEY
    private var isTestModeEnabled: Boolean = true

    // Active Checkout Listener and Metadata
    private var checkoutListener: RazorpayCheckoutListener? = null
    private var activeTransactionMeta: RazorpayPaymentManager.ActivePaymentMeta? = null

    // Reactive Flows
    private val _paymentState = MutableStateFlow<PaymentFlowState>(PaymentFlowState.Idle)
    val paymentState: StateFlow<PaymentFlowState> = _paymentState.asStateFlow()

    private val _paymentResultEvents = MutableSharedFlow<PaymentResultData>(extraBufferCapacity = 10)
    val paymentResultEvents: SharedFlow<PaymentResultData> = _paymentResultEvents.asSharedFlow()

    /**
     * Initializes the Razorpay SDK instance and preloads core checkout assets in the background.
     */
    fun initializeSdk(
        context: Context,
        customKeyId: String? = null,
        isTestMode: Boolean = true
    ) {
        try {
            _paymentState.value = PaymentFlowState.Initializing(isTestMode)
            isTestModeEnabled = isTestMode
            activeKeyId = customKeyId ?: if (isTestMode) DEFAULT_TEST_KEY else DEFAULT_LIVE_KEY

            // Preload Razorpay Webview and SDK core resources for snappy launch
            Checkout.preload(context.applicationContext)
            isInitialized = true
            _paymentState.value = PaymentFlowState.Ready(activeKeyId)
            Log.i(TAG, "Razorpay SDK Initialized successfully. Mode: ${if (isTestMode) "TEST" else "LIVE"}, Key: $activeKeyId")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Razorpay SDK: ${e.message}", e)
            _paymentState.value = PaymentFlowState.Failed(
                PaymentResultData(
                    isSuccess = false,
                    paymentId = null,
                    errorCode = -1,
                    errorMessage = "SDK Initialization failed: ${e.message}",
                    amount = 0.0
                )
            )
        }
    }

    /**
     * Checks if the SDK has been initialized.
     */
    fun isReady(): Boolean = isInitialized

    /**
     * Registers a checkout listener to receive lifecycle events and callbacks.
     */
    fun registerCheckoutListener(listener: RazorpayCheckoutListener) {
        this.checkoutListener = listener
        Log.d(TAG, "Registered new Razorpay checkout listener: ${listener.javaClass.simpleName}")
    }

    /**
     * Unregisters the active checkout listener.
     */
    fun unregisterCheckoutListener() {
        this.checkoutListener = null
        Log.d(TAG, "Unregistered active checkout listener")
    }

    /**
     * Builds standard, secure JSON payment options for Razorpay Checkout.
     */
    fun prepareCheckoutOptions(payload: RazorpayPaymentPayload): JSONObject {
        val options = JSONObject()
        options.put("key", payload.key)
        options.put("name", payload.name)
        options.put("description", payload.description)
        options.put("currency", payload.currency)
        options.put("amount", payload.amountPaise)
        options.put("send_sms_hash", true)

        // Esports Neon Theming
        val theme = JSONObject()
        theme.put("color", payload.themeColor)
        theme.put("backdrop_color", "#0B0E17")
        options.put("theme", theme)

        // Customer Prefill
        val prefill = JSONObject()
        prefill.put("email", payload.prefillEmail)
        prefill.put("contact", payload.prefillContact)
        options.put("prefill", prefill)

        // Metadata notes for backend webhook & ledger reconciliation
        val notesJson = JSONObject()
        payload.notes.forEach { (k, v) ->
            notesJson.put(k, v)
        }
        notesJson.put("service", "RazorpayIntegrationService_2.0")
        notesJson.put("platform", "Android_Jetpack_Compose")
        options.put("notes", notesJson)

        // Auto retry options
        val retry = JSONObject()
        retry.put("enabled", true)
        retry.put("max_count", 3)
        options.put("retry", retry)

        return options
    }

    /**
     * Prepares and starts the Razorpay Checkout Flow for a Tournament Entry Fee.
     */
    fun prepareAndLaunchTournamentCheckout(
        activity: Activity,
        tournament: TournamentEntity,
        team: TeamEntity,
        user: UserEntity?,
        isTestMode: Boolean = isTestModeEnabled,
        listener: RazorpayCheckoutListener? = null
    ) {
        val key = if (isTestMode) DEFAULT_TEST_KEY else DEFAULT_LIVE_KEY
        val amountInPaise = (tournament.entryFee * 100).toLong()
        val email = user?.email?.takeIf { it.isNotBlank() } ?: "player@pgesports.in"
        val mobile = user?.mobile?.takeIf { it.isNotBlank() } ?: "+919876543210"
        val orderId = "order_TOUR_" + UUID.randomUUID().toString().take(10).uppercase()

        activeTransactionMeta = RazorpayPaymentManager.ActivePaymentMeta(
            type = PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
            amount = tournament.entryFee,
            tournamentId = tournament.id,
            tournamentTitle = tournament.title,
            teamId = team.id,
            teamName = team.name,
            userId = user?.id
        )

        if (listener != null) {
            this.checkoutListener = listener
        }

        val payload = RazorpayPaymentPayload(
            key = key,
            amountPaise = amountInPaise,
            currency = "INR",
            name = "PG E-SPORTS 2.0",
            description = "Entry: ${tournament.title} (Slot Check-in)",
            prefillEmail = email,
            prefillContact = mobile,
            themeColor = "#00FF66",
            notes = mapOf(
                "order_id" to orderId,
                "tournament_id" to tournament.id,
                "tournament_title" to tournament.title,
                "team_id" to team.id,
                "team_name" to team.name,
                "player_uid" to (user?.freeFireUid ?: "UNKNOWN"),
                "player_ign" to (user?.freeFireIgn ?: "UNKNOWN"),
                "user_id" to (user?.id ?: "GUEST")
            )
        )

        _paymentState.value = PaymentFlowState.Processing(
            orderId = orderId,
            amount = tournament.entryFee,
            description = payload.description
        )

        checkoutListener?.onPaymentInitiated(orderId, payload)

        try {
            val checkout = Checkout()
            checkout.setKeyID(key)
            val options = prepareCheckoutOptions(payload)
            checkout.open(activity, options)
            Log.d(TAG, "Launched Razorpay Tournament Checkout for Order: $orderId, Tour: ${tournament.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch Razorpay checkout: ${e.message}", e)
            val errorData = PaymentResultData(
                isSuccess = false,
                paymentId = null,
                orderId = orderId,
                errorCode = -1,
                errorMessage = e.message ?: "Failed to open checkout activity",
                amount = tournament.entryFee,
                type = PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
                tournamentId = tournament.id,
                tournamentTitle = tournament.title,
                teamId = team.id,
                teamName = team.name
            )
            dispatchPaymentError(errorData)
        }
    }

    /**
     * Prepares and starts the Razorpay Checkout Flow for a Wallet Deposit.
     */
    fun prepareAndLaunchWalletDeposit(
        activity: Activity,
        amount: Double,
        user: UserEntity?,
        isTestMode: Boolean = isTestModeEnabled,
        listener: RazorpayCheckoutListener? = null
    ) {
        val key = if (isTestMode) DEFAULT_TEST_KEY else DEFAULT_LIVE_KEY
        val amountInPaise = (amount * 100).toLong()
        val email = user?.email?.takeIf { it.isNotBlank() } ?: "player@pgesports.in"
        val mobile = user?.mobile?.takeIf { it.isNotBlank() } ?: "+919876543210"
        val orderId = "order_WAL_" + UUID.randomUUID().toString().take(10).uppercase()

        activeTransactionMeta = RazorpayPaymentManager.ActivePaymentMeta(
            type = PaymentTransactionType.WALLET_DEPOSIT,
            amount = amount,
            userId = user?.id
        )

        if (listener != null) {
            this.checkoutListener = listener
        }

        val payload = RazorpayPaymentPayload(
            key = key,
            amountPaise = amountInPaise,
            currency = "INR",
            name = "PG E-SPORTS 2.0",
            description = "PG Wallet Instant Deposit (₹${amount.toInt()})",
            prefillEmail = email,
            prefillContact = mobile,
            themeColor = "#00FF66",
            notes = mapOf(
                "order_id" to orderId,
                "transaction_type" to "WALLET_DEPOSIT",
                "player_uid" to (user?.freeFireUid ?: "UNKNOWN"),
                "user_id" to (user?.id ?: "GUEST")
            )
        )

        _paymentState.value = PaymentFlowState.Processing(
            orderId = orderId,
            amount = amount,
            description = payload.description
        )

        checkoutListener?.onPaymentInitiated(orderId, payload)

        try {
            val checkout = Checkout()
            checkout.setKeyID(key)
            val options = prepareCheckoutOptions(payload)
            checkout.open(activity, options)
            Log.d(TAG, "Launched Razorpay Wallet Deposit Checkout for Order: $orderId, Amount: ₹$amount")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch Razorpay wallet checkout: ${e.message}", e)
            val errorData = PaymentResultData(
                isSuccess = false,
                paymentId = null,
                orderId = orderId,
                errorCode = -1,
                errorMessage = e.message ?: "Failed to open wallet deposit checkout",
                amount = amount,
                type = PaymentTransactionType.WALLET_DEPOSIT
            )
            dispatchPaymentError(errorData)
        }
    }

    /**
     * Prepares and starts the Razorpay Checkout Flow for Free Fire Diamond Top-Ups.
     */
    fun prepareAndLaunchDiamondTopUp(
        activity: Activity,
        packName: String,
        amount: Double,
        user: UserEntity?,
        isTestMode: Boolean = isTestModeEnabled,
        listener: RazorpayCheckoutListener? = null
    ) {
        val key = if (isTestMode) DEFAULT_TEST_KEY else DEFAULT_LIVE_KEY
        val amountInPaise = (amount * 100).toLong()
        val email = user?.email?.takeIf { it.isNotBlank() } ?: "player@pgesports.in"
        val mobile = user?.mobile?.takeIf { it.isNotBlank() } ?: "+919876543210"
        val orderId = "order_FFD_" + UUID.randomUUID().toString().take(10).uppercase()

        activeTransactionMeta = RazorpayPaymentManager.ActivePaymentMeta(
            type = PaymentTransactionType.WALLET_DEPOSIT,
            amount = amount,
            userId = user?.id
        )

        if (listener != null) {
            this.checkoutListener = listener
        }

        val payload = RazorpayPaymentPayload(
            key = key,
            amountPaise = amountInPaise,
            currency = "INR",
            name = "Free Fire Diamond Top-Up",
            description = "Direct Top-Up: $packName (UID: ${user?.freeFireUid ?: "1928374650"})",
            prefillEmail = email,
            prefillContact = mobile,
            themeColor = "#FF5722",
            notes = mapOf(
                "order_id" to orderId,
                "pack_name" to packName,
                "player_ff_uid" to (user?.freeFireUid ?: "1928374650"),
                "player_ff_ign" to (user?.freeFireIgn ?: "PG_THUNDER_99"),
                "server_region" to (user?.freeFireServerRegion ?: "IND")
            )
        )

        _paymentState.value = PaymentFlowState.Processing(
            orderId = orderId,
            amount = amount,
            description = payload.description
        )

        checkoutListener?.onPaymentInitiated(orderId, payload)

        try {
            val checkout = Checkout()
            checkout.setKeyID(key)
            val options = prepareCheckoutOptions(payload)
            checkout.open(activity, options)
        } catch (e: Exception) {
            val errorData = PaymentResultData(
                isSuccess = false,
                paymentId = null,
                orderId = orderId,
                errorCode = -1,
                errorMessage = e.message ?: "Failed to open diamond checkout",
                amount = amount,
                type = PaymentTransactionType.WALLET_DEPOSIT
            )
            dispatchPaymentError(errorData)
        }
    }

    /**
     * Primary callback handler invoked by Activity's onPaymentSuccess.
     */
    fun onPaymentSuccessCallback(razorpayPaymentId: String?, paymentData: PaymentData?) {
        Log.i(TAG, "SDK onPaymentSuccess: PaymentID=$razorpayPaymentId, OrderID=${paymentData?.orderId}")
        val meta = activeTransactionMeta
        val finalPaymentId = razorpayPaymentId ?: paymentData?.paymentId ?: ("PAY_RZP_" + UUID.randomUUID().toString().take(8).uppercase())

        val result = PaymentResultData(
            isSuccess = true,
            paymentId = finalPaymentId,
            orderId = paymentData?.orderId,
            signature = paymentData?.signature,
            amount = meta?.amount ?: 0.0,
            type = meta?.type ?: PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
            tournamentId = meta?.tournamentId,
            tournamentTitle = meta?.tournamentTitle,
            teamId = meta?.teamId,
            teamName = meta?.teamName,
            paymentMethod = "Razorpay Native Gateway",
            timestamp = System.currentTimeMillis()
        )

        _paymentState.value = PaymentFlowState.Success(result)
        serviceScope.launch {
            _paymentResultEvents.emit(result)
        }

        checkoutListener?.onPaymentSuccess(result)
        cleanupActiveTransaction()
    }

    /**
     * Primary callback handler invoked by Activity's onPaymentError.
     */
    fun onPaymentErrorCallback(errorCode: Int, errorDescription: String?, paymentData: PaymentData?) {
        Log.e(TAG, "SDK onPaymentError: Code=$errorCode, Desc=$errorDescription, PaymentID=${paymentData?.paymentId}")
        val meta = activeTransactionMeta

        val readableMessage = when (errorCode) {
            Checkout.PAYMENT_CANCELED -> "Payment was cancelled by the player."
            Checkout.NETWORK_ERROR -> "Network connection error occurred during gateway communication. Please check your internet."
            Checkout.INVALID_OPTIONS -> "Invalid Razorpay options configured. Please retry."
            Checkout.TLS_ERROR -> "Device TLS security version not supported."
            else -> errorDescription ?: "Transaction was declined or failed. Please retry."
        }

        val errorResult = PaymentResultData(
            isSuccess = false,
            paymentId = paymentData?.paymentId,
            orderId = paymentData?.orderId,
            signature = paymentData?.signature,
            errorCode = errorCode,
            errorMessage = readableMessage,
            amount = meta?.amount ?: 0.0,
            type = meta?.type ?: PaymentTransactionType.TOURNAMENT_ENTRY_FEE,
            tournamentId = meta?.tournamentId,
            tournamentTitle = meta?.tournamentTitle,
            teamId = meta?.teamId,
            teamName = meta?.teamName,
            paymentMethod = "Razorpay Native Gateway",
            timestamp = System.currentTimeMillis()
        )

        if (errorCode == Checkout.PAYMENT_CANCELED) {
            _paymentState.value = PaymentFlowState.Cancelled(readableMessage)
            checkoutListener?.onPaymentCancelled(readableMessage)
        } else {
            _paymentState.value = PaymentFlowState.Failed(errorResult)
            checkoutListener?.onPaymentError(errorResult)
        }

        serviceScope.launch {
            _paymentResultEvents.emit(errorResult)
        }

        cleanupActiveTransaction()
    }

    private fun dispatchPaymentError(errorData: PaymentResultData) {
        _paymentState.value = PaymentFlowState.Failed(errorData)
        serviceScope.launch {
            _paymentResultEvents.emit(errorData)
        }
        checkoutListener?.onPaymentError(errorData)
        cleanupActiveTransaction()
    }

    private fun cleanupActiveTransaction() {
        activeTransactionMeta = null
        // Maintain checkoutListener reference until next lifecycle or explicit unregister
    }

    /**
     * Resets the payment flow state back to Idle.
     */
    fun resetState() {
        _paymentState.value = if (isInitialized) PaymentFlowState.Ready(activeKeyId) else PaymentFlowState.Idle
    }
}
