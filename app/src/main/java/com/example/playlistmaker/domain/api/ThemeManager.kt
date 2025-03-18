package com.example.playlistmaker.domain.api

interface ThemeManager {
    fun getCurrentTheme(): String
    fun switchTheme(themeMode: String)
}
