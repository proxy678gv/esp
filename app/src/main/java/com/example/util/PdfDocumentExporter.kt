package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PdfDocumentExporter {

    /**
     * Generates a high-quality multi-page PDF specification document for PG E-SPORTS 2.0
     * in the dark minimalist esports technology design style.
     */
    fun generateProductSpecPdf(context: Context): File? {
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // A4 standard width (pt)
        val pageHeight = 842 // A4 standard height (pt)

        val bgPaint = Paint().apply { color = Color.parseColor("#0B0E17") }
        val cardBgPaint = Paint().apply { color = Color.parseColor("#131927") }
        val borderPaint = Paint().apply {
            color = Color.parseColor("#1F293D")
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        val neonGreenPaint = Paint().apply {
            color = Color.parseColor("#00FF66")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val purplePaint = Paint().apply {
            color = Color.parseColor("#9D4EDD")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val whiteBoldPaint = Paint().apply {
            color = Color.WHITE
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val whiteRegularPaint = Paint().apply {
            color = Color.parseColor("#E0E0E0")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        val mutedTextPaint = Paint().apply {
            color = Color.parseColor("#8E99A8")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }
        val goldPaint = Paint().apply {
            color = Color.parseColor("#FFD700")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }

        // ================= PAGE 1: Executive Overview, Core Concept & Account Flow =================
        val pageInfo1 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page1 = pdfDocument.startPage(pageInfo1)
        val canvas1 = page1.canvas
        canvas1.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

        // Header Accent Bar
        val accentBarPaint = Paint().apply { color = Color.parseColor("#00FF66") }
        canvas1.drawRect(30f, 30f, (pageWidth - 30).toFloat(), 34f, accentBarPaint)

        // Document Title
        whiteBoldPaint.textSize = 22f
        canvas1.drawText("PG E-SPORTS 2.0", 30f, 65f, whiteBoldPaint)

        neonGreenPaint.textSize = 12f
        canvas1.drawText("PLAYER + VERIFIED HOST TOURNAMENT PLATFORM", 30f, 82f, neonGreenPaint)

        mutedTextPaint.textSize = 10f
        canvas1.drawText("Tagline: Play. Compete. Win.  |  Technical Product Specification", 30f, 96f, mutedTextPaint)

        // Divider
        canvas1.drawLine(30f, 106f, (pageWidth - 30).toFloat(), 106f, borderPaint)

        var currentY = 126f

        // Section 1: Core Concept
        drawSectionHeader(canvas1, "1. CORE CONCEPT & ROLE ARCHITECTURE", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val box1 = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 110f)
        canvas1.drawRoundRect(box1, 8f, 8f, cardBgPaint)
        canvas1.drawRoundRect(box1, 8f, 8f, borderPaint)

        whiteRegularPaint.textSize = 9.5f
        val conceptLines = listOf(
            "• Single Unified Account: Every user registers as a standard PLAYER by default.",
            "• Dual-Mode Architecture: Approved hosts seamlessly switch between [🎮 Player Mode] & [🛡️ Host Mode].",
            "• Player Capabilities: Browse Free Fire tournaments, join squad/solo matches, pay entry fees via Razorpay,",
            "  receive 15-min match alarm notifications, access Room ID/Password, compete, and receive instant prize payouts.",
            "• Host Eligibility: Players apply for Host Mode. Super Admin audits UID & credentials before granting Verified Host status."
        )
        var lineY = currentY + 20f
        conceptLines.forEach { line ->
            canvas1.drawText(line, 42f, lineY, whiteRegularPaint)
            lineY += 18f
        }
        currentY += 124f

        // Section 2: Account Lifecycle Flow Diagram
        drawSectionHeader(canvas1, "2. ACCOUNT LIFECYCLE & ROLE TRANSITION", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val flowBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 60f)
        canvas1.drawRoundRect(flowBox, 8f, 8f, cardBgPaint)
        canvas1.drawRoundRect(flowBox, 8f, 8f, borderPaint)

        val stepPaint = Paint().apply {
            color = Color.parseColor("#00FF66")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 9f
        }
        val flowText = "PLAYER ACCOUNT  →  Apply for Host  →  Super Admin Review  →  Approved  →  HOST MODE UNLOCKED"
        canvas1.drawText(flowText, 45f, currentY + 34f, stepPaint)
        currentY += 74f

        // Section 3: Player Tournament Workflow
        drawSectionHeader(canvas1, "3. END-TO-END PLAYER TOURNAMENT WORKFLOW", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val wfBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 130f)
        canvas1.drawRoundRect(wfBox, 8f, 8f, cardBgPaint)
        canvas1.drawRoundRect(wfBox, 8f, 8f, borderPaint)

        val steps = listOf(
            "1. Browse Tournaments   → Filter by Solo/Duo/Squad, Entry Fee, Prize Pool, Schedule",
            "2. Select & Inspect     → Review Match Rules, Map (Bermuda/Purgatory), Kill & Booyah Rewards",
            "3. Razorpay Fast Pay   → UPI / Cards / NetBanking / Wallet (Direct Escrow Integration)",
            "4. Slot Confirmation   → Unique Slot Assigned & Automated 15-Min Local Notification Set",
            "5. 15-Min Match Alert   → Alarm sounds, Room ID & Password auto-revealed to Captain",
            "6. Booyah & Payout      → Server-side result audit; winnings instantly credited to PG Wallet"
        )
        lineY = currentY + 20f
        steps.forEach { step ->
            canvas1.drawText(step, 42f, lineY, whiteRegularPaint)
            lineY += 18f
        }
        currentY += 144f

        // Section 4: Host Permissions Matrix
        drawSectionHeader(canvas1, "4. HOST PERMISSIONS & OPERATIONAL BOUNDARIES", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val permBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 130f)
        canvas1.drawRoundRect(permBox, 8f, 8f, cardBgPaint)
        canvas1.drawRoundRect(permBox, 8f, 8f, borderPaint)

        neonGreenPaint.textSize = 10f
        canvas1.drawText("✅ ALLOWED FOR APPROVED HOSTS:", 42f, currentY + 20f, neonGreenPaint)
        whiteRegularPaint.textSize = 9f
        canvas1.drawText("• Create/Edit own tournaments (Solo/Duo/Squad, Kill & Booyah Rewards, Match Rules)", 42f, currentY + 36f, whiteRegularPaint)
        canvas1.drawText("• Manage participants, assign slots, input Room ID & Password, publish live match scores", 42f, currentY + 52f, whiteRegularPaint)

        val redPaint = Paint().apply {
            color = Color.parseColor("#FF3366")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 10f
        }
        canvas1.drawText("⛔ STRICTLY PROHIBITED FOR HOSTS (SECURITY RESTRICTIONS):", 42f, currentY + 76f, redPaint)
        canvas1.drawText("• CANNOT edit other hosts' tournaments (Enforced: loggedInUser.id == tournament.hostId)", 42f, currentY + 92f, whiteRegularPaint)
        canvas1.drawText("• CANNOT approve other hosts, modify payment ledger, or withdraw player entry fees directly", 42f, currentY + 108f, whiteRegularPaint)

        // Footer Page 1
        canvas1.drawText("PG E-SPORTS 2.0  •  Page 1 of 3", (pageWidth / 2 - 50).toFloat(), (pageHeight - 20).toFloat(), mutedTextPaint)
        pdfDocument.finishPage(page1)

        // ================= PAGE 2: Host Tournament Lifecycle, Verification & Role Matrix =================
        val pageInfo2 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 2).create()
        val page2 = pdfDocument.startPage(pageInfo2)
        val canvas2 = page2.canvas
        canvas2.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

        currentY = 40f
        drawSectionHeader(canvas2, "5. HOST TOURNAMENT CREATION & LIFECYCLE", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val hostBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 170f)
        canvas2.drawRoundRect(hostBox, 8f, 8f, cardBgPaint)
        canvas2.drawRoundRect(hostBox, 8f, 8f, borderPaint)

        goldPaint.textSize = 10f
        canvas2.drawText("HOST DASHBOARD METRICS:", 42f, currentY + 20f, goldPaint)
        whiteRegularPaint.textSize = 9f
        canvas2.drawText("Total Earnings  |  Total Tournaments  |  Active Matches  |  Completed  |  Total Participants  |  [CREATE TOURNAMENT]", 42f, currentY + 36f, whiteRegularPaint)

        goldPaint.textSize = 10f
        canvas2.drawText("TOURNAMENT CONFIGURATION FIELDS:", 42f, currentY + 60f, goldPaint)
        val fields = listOf(
            "• Game: Garena Free Fire (MAX)  |  Format: Solo / Duo / Squad  |  Map: Bermuda / Purgatory / Alpine",
            "• Financials: Entry Fee (₹), Guaranteed Prize Pool (₹), Per Kill Bounty (₹), Booyah 1st Place Bonus (₹)",
            "• Operational: Max Participants (48 Solo / 12 Squads), Date & Time, Check-In Window, Custom Rules",
            "• Credentials: Room ID & Password (Encrypted until 15 minutes before start time)"
        )
        lineY = currentY + 76f
        fields.forEach { f ->
            canvas2.drawText(f, 42f, lineY, whiteRegularPaint)
            lineY += 16f
        }

        neonGreenPaint.textSize = 9.5f
        canvas2.drawText("LIFECYCLE STATUS PROGRESSION:", 42f, currentY + 148f, neonGreenPaint)
        whiteRegularPaint.textSize = 8.5f
        canvas2.drawText("Draft → Published → Registration Open → Full → Live Match → Completed → Results Verified", 42f, currentY + 162f, whiteRegularPaint)
        currentY += 184f

        // Section 6: Host Verification System
        drawSectionHeader(canvas2, "6. HOST VERIFICATION & SUPER ADMIN AUDIT", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val verBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 100f)
        canvas2.drawRoundRect(verBox, 8f, 8f, cardBgPaint)
        canvas2.drawRoundRect(verBox, 8f, 8f, borderPaint)

        whiteRegularPaint.textSize = 9f
        canvas2.drawText("Application Criteria: Full Name, Phone, Email, Free Fire UID, Experience Level, Expected Tournaments/Month.", 42f, currentY + 20f, whiteRegularPaint)
        canvas2.drawText("Super Admin Review Controls: [ APPROVE ]  |  [ REJECT ]  |  [ SUSPEND HOST ]", 42f, currentY + 38f, goldPaint)
        canvas2.drawText("Verified Host Badge: ✅ VERIFIED HOST + Unique Cryptographic Host ID assigned upon approval.", 42f, currentY + 56f, neonGreenPaint)
        canvas2.drawText("Suspension Policy: Any reported fake scores or unauthorized room delays trigger instant host revocation.", 42f, currentY + 74f, mutedTextPaint)
        currentY += 114f

        // Section 7: Role Permissions Matrix Table
        drawSectionHeader(canvas2, "7. ROLE PERMISSIONS COMPARISON MATRIX", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val tableBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 170f)
        canvas2.drawRoundRect(tableBox, 8f, 8f, cardBgPaint)
        canvas2.drawRoundRect(tableBox, 8f, 8f, borderPaint)

        // Table Header
        canvas2.drawRect(30f, currentY, (pageWidth - 30).toFloat(), currentY + 26f, borderPaint)
        whiteBoldPaint.textSize = 9f
        canvas2.drawText("CAPABILITY", 40f, currentY + 17f, whiteBoldPaint)
        canvas2.drawText("PLAYER", 240f, currentY + 17f, whiteBoldPaint)
        canvas2.drawText("HOST", 340f, currentY + 17f, whiteBoldPaint)
        canvas2.drawText("SUPER ADMIN", 440f, currentY + 17f, whiteBoldPaint)

        val matrix = listOf(
            Triple("Join Tournaments & Pay Entry", "✅ Yes", "✅ Yes (as Player)"),
            Triple("Receive 15m Match Alarms & Room Keys", "✅ Yes", "✅ Yes"),
            Triple("Create & Edit Own Tournaments", "❌ No", "✅ Yes"),
            Triple("Input Room ID / Publish Results", "❌ No", "✅ Yes"),
            Triple("Edit Other Hosts' Matches", "❌ No", "❌ Prohibited"),
            Triple("Approve / Suspend Hosts", "❌ No", "❌ No"),
            Triple("Platform Financial Administration", "❌ No", "❌ No")
        )
        lineY = currentY + 44f
        matrix.forEach { (cap, player, host) ->
            canvas2.drawText(cap, 40f, lineY, whiteRegularPaint)
            canvas2.drawText(player, 240f, lineY, if (player.startsWith("✅")) neonGreenPaint else mutedTextPaint)
            canvas2.drawText(host, 340f, lineY, if (host.startsWith("✅")) neonGreenPaint else redPaint)
            canvas2.drawText("✅ Full Admin", 440f, lineY, goldPaint)
            lineY += 18f
        }

        // Footer Page 2
        canvas2.drawText("PG E-SPORTS 2.0  •  Page 2 of 3", (pageWidth / 2 - 50).toFloat(), (pageHeight - 20).toFloat(), mutedTextPaint)
        pdfDocument.finishPage(page2)

        // ================= PAGE 3: Payment Architecture, Security & Database Schemas =================
        val pageInfo3 = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 3).create()
        val page3 = pdfDocument.startPage(pageInfo3)
        val canvas3 = page3.canvas
        canvas3.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

        currentY = 40f
        drawSectionHeader(canvas3, "8. PAYMENT ARCHITECTURE & ESCROW SECURITY", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val payBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 130f)
        canvas3.drawRoundRect(payBox, 8f, 8f, cardBgPaint)
        canvas3.drawRoundRect(payBox, 8f, 8f, borderPaint)

        val payFlow = "PLAYER  →  RAZORPAY GATEWAY  →  PG TRANSACTION LEDGER  →  SLOT CONFIRMATION"
        canvas3.drawText(payFlow, 42f, currentY + 22f, neonGreenPaint)
        val payFlow2 = "        →  MATCH COMPLETED  →  RESULT AUDIT  →  WINNER BALANCE  →  UPI/BANK WITHDRAWAL"
        canvas3.drawText(payFlow2, 42f, currentY + 40f, neonGreenPaint)

        whiteRegularPaint.textSize = 9f
        canvas3.drawText("• Critical Financial Control: Tournament entry fees are held securely in platform escrow.", 42f, currentY + 68f, whiteRegularPaint)
        canvas3.drawText("  Hosts NEVER receive direct custody of player entry funds. All payouts require automated ledger verification.", 42f, currentY + 84f, whiteRegularPaint)
        canvas3.drawText("• Supported Gateways: Razorpay UPI (GPay, PhonePe, Paytm, CRED), NetBanking, Cards, and PG Diamond Vault.", 42f, currentY + 100f, whiteRegularPaint)
        currentY += 144f

        // Section 9: Backend Security & Ownership Validation
        drawSectionHeader(canvas3, "9. BACKEND SECURITY & ACCESS CONTROL (RBAC)", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val secBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 90f)
        canvas3.drawRoundRect(secBox, 8f, 8f, cardBgPaint)
        canvas3.drawRoundRect(secBox, 8f, 8f, borderPaint)

        val secFlow = "Incoming Request  →  JWT Auth Check  →  Host Approval Check  →  Ownership Check  →  Execute"
        canvas3.drawText(secFlow, 42f, currentY + 22f, goldPaint)
        whiteRegularPaint.textSize = 9f
        canvas3.drawText("• Hardened Rule: A Host can edit a tournament ONLY when: loggedInUser.id == tournament.hostId", 42f, currentY + 44f, whiteRegularPaint)
        canvas3.drawText("• Zero Trust Architecture: All scoring, slot assignments, and payouts are validated on Node.js/Express backend.", 42f, currentY + 60f, whiteRegularPaint)
        currentY += 104f

        // Section 10: Suggested Database Schemas (MongoDB & Room SQLite)
        drawSectionHeader(canvas3, "10. SUGGESTED DATABASE SCHEMAS (MONGODB & ROOM)", currentY, purplePaint, whiteBoldPaint)
        currentY += 24f

        val dbBox = RectF(30f, currentY, (pageWidth - 30).toFloat(), currentY + 210f)
        canvas3.drawRoundRect(dbBox, 8f, 8f, cardBgPaint)
        canvas3.drawRoundRect(dbBox, 8f, 8f, borderPaint)

        val schemaPaint = Paint().apply {
            color = Color.parseColor("#3395FF")
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textSize = 8.5f
        }
        val schemaCode = listOf(
            "// USER SCHEMA",
            "{ id, googleId, email, role: 'PLAYER'|'HOST'|'ADMIN', hostStatus: 'NONE'|'PENDING'|'APPROVED', walletBalance, rating }",
            "",
            "// TOURNAMENT SCHEMA",
            "{ id, hostId, title, game: 'FREE_FIRE', type: 'SOLO'|'DUO'|'SQUAD', entryFee, prizePool, perKillReward, booyahReward,",
            "  maxSlots, rules, scheduleMillis, roomId, roomPassword, status: 'OPEN'|'LIVE'|'COMPLETED'|'VERIFIED', results: [] }",
            "",
            "// PARTICIPANT SCHEMA",
            "{ tournamentId, playerId, teamId, slotNumber, paymentId, registeredAt, isCheckInDone, killCount, placementRank }",
            "",
            "// TRANSACTION LEDGER SCHEMA",
            "{ id, userId, tournamentId, type: 'DEPOSIT'|'ENTRY_FEE'|'PRIZE_PAYOUT'|'WITHDRAW', amount, status, gatewayTxnId, timestamp }"
        )
        lineY = currentY + 20f
        schemaCode.forEach { code ->
            if (code.startsWith("//")) {
                canvas3.drawText(code, 42f, lineY, goldPaint)
            } else {
                canvas3.drawText(code, 42f, lineY, schemaPaint)
            }
            lineY += 14f
        }
        currentY += 224f

        // Technology Stack Summary
        val techPaint = Paint().apply {
            color = Color.parseColor("#00FF66")
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            textSize = 8.5f
        }
        canvas3.drawText("TECH STACK: Android Jetpack Compose | Node.js + Express API | MongoDB / Room SQLite | Razorpay SDK | RBAC", 30f, currentY + 10f, techPaint)

        // Footer Page 3
        canvas3.drawText("PG E-SPORTS 2.0  •  Page 3 of 3  •  Play. Compete. Win.", (pageWidth / 2 - 90).toFloat(), (pageHeight - 20).toFloat(), mutedTextPaint)
        pdfDocument.finishPage(page3)

        // Save PDF to cache dir
        return try {
            val file = File(context.cacheDir, "PG_ESPORTS_2.0_Specification.pdf")
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDocument.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            null
        }
    }

    private fun drawSectionHeader(canvas: Canvas, title: String, y: Float, tagPaint: Paint, titlePaint: Paint) {
        titlePaint.textSize = 12f
        canvas.drawText(title, 30f, y, titlePaint)
    }

    fun shareGeneratedPdf(context: Context, pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "PG E-SPORTS 2.0 - Technical Product Specification")
                putExtra(Intent.EXTRA_TEXT, "Here is the complete PG E-SPORTS 2.0 Player + Verified Host platform specification PDF.")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share PG E-SPORTS 2.0 PDF Specification"))
        } catch (e: Exception) {
            // Fallback generic send
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_SUBJECT, "PG E-SPORTS 2.0 Specification")
            }
            context.startActivity(Intent.createChooser(fallbackIntent, "Export PDF"))
        }
    }
}
