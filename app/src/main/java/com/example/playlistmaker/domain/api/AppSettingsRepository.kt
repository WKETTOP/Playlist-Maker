package com.example.playlistmaker.domain.api

interface AppSettingsRepository {
    fun getThemeMode(): String
    fun saveThemeMode(themeMode: String)
    fun applyTheme()
}