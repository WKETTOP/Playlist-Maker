package com.example.playlistmaker.domain.api

interface ThemeManager {
    fun isDarkThemeEnabled(): Boolean
    fun switchTheme(isDarkTheme: Boolean)
}
