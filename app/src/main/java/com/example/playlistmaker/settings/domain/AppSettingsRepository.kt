package com.example.playlistmaker.settings.domain

interface AppSettingsRepository {
    fun getThemeMode(): String
    fun saveThemeMode(themeMode: String)
}
