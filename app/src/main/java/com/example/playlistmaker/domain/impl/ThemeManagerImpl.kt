package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.AppSettingsRepository
import com.example.playlistmaker.domain.api.ThemeManager

class ThemeManagerImpl(private val appSettingsRepository: AppSettingsRepository) : ThemeManager {
    override fun isDarkThemeEnabled(): Boolean {
        return appSettingsRepository.getThemeMode() == "dark"
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        val newMode = if (isDarkTheme) "dark" else "light"
        appSettingsRepository.saveThemeMode(newMode)
        appSettingsRepository.applyTheme()
    }

    override fun getCurrentThemeMode(): String {
        return appSettingsRepository.getThemeMode()
    }

}
