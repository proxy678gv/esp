package com.example.data.localization

enum class AppLanguage {
    ENGLISH,
    TAMIL
}

object PGStrings {
    fun appTagline(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Play. Compete. Win."
        AppLanguage.TAMIL -> "விளையாடு. போட்டியிடு. வெற்றி பெறு."
    }

    fun home(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Home"
        AppLanguage.TAMIL -> "முகப்பு"
    }

    fun tournaments(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Tournaments"
        AppLanguage.TAMIL -> "போட்டிகள்"
    }

    fun myTournaments(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "My Matches"
        AppLanguage.TAMIL -> "என் போட்டிகள்"
    }

    fun pgRank(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "PG Rank"
        AppLanguage.TAMIL -> "மதிப்பீடு"
    }

    fun teams(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Teams"
        AppLanguage.TAMIL -> "அணிகள்"
    }

    fun wallet(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Wallet"
        AppLanguage.TAMIL -> "பணப்பை"
    }

    fun notifications(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Notifications"
        AppLanguage.TAMIL -> "அறிவிப்புகள்"
    }

    fun hostHub(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Host Hub"
        AppLanguage.TAMIL -> "நடத்துனர் தளம்"
    }

    fun adminConsole(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Admin Console"
        AppLanguage.TAMIL -> "நிர்வாகக் குழு"
    }

    fun joinNow(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Join Tournament"
        AppLanguage.TAMIL -> "இப்போதே சேரவும்"
    }

    fun checkIn(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Check-In Squad"
        AppLanguage.TAMIL -> "வருகை பதிவுசெய்"
    }

    fun matchRoom(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Match Room"
        AppLanguage.TAMIL -> "போட்டி அறை"
    }

    fun prizePool(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Prize Pool"
        AppLanguage.TAMIL -> "பரிசுத் தொகை"
    }

    fun entryFee(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Entry Fee"
        AppLanguage.TAMIL -> "நுழைவுக் கட்டணம்"
    }

    fun rules(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Official Rules"
        AppLanguage.TAMIL -> "விதிமுறைகள்"
    }
}
