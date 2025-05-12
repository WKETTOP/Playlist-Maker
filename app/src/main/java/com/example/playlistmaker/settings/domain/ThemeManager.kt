package com.example.playlistmaker.settings.domain

interface ThemeManager {
    fun isDarkThemeEnabledNow(): Boolean
    fun switchThemeSettings(isDarkEnabled: Boolean)
    fun getCurrentThemeMode(): String
    fun applySavedTheme()
}
