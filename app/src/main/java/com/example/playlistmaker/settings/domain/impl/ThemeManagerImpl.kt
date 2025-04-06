package com.example.playlistmaker.settings.domain.impl

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.data.impl.AppSettingsKeys
import com.example.playlistmaker.settings.domain.AppSettingsRepository
import com.example.playlistmaker.settings.domain.ThemeManager

class ThemeManagerImpl(
    private val appSettingsRepository: AppSettingsRepository,
    private val context: Context,
) : ThemeManager {

    override fun isDarkThemeEnabledNow(): Boolean {
        return when (appSettingsRepository.getThemeMode()) {
            AppSettingsKeys.DARK_MODE -> true
            AppSettingsKeys.LIGHT_MODE -> false
            AppSettingsKeys.FOLLOW_SYSTEM_MODE_KEY -> isSystemDarkThemeEnabled()
            else -> isSystemDarkThemeEnabled()
        }
    }

    override fun switchThemeSettings(isDarkEnabled: Boolean) {
        val newMode = if (isDarkEnabled) AppSettingsKeys.DARK_MODE else AppSettingsKeys.LIGHT_MODE
        appSettingsRepository.saveThemeMode(newMode)
        applyTheme(newMode)
    }

    override fun getCurrentThemeMode(): String {
        return appSettingsRepository.getThemeMode()
    }

    override fun applySavedTheme() {
        val themeMode = getCurrentThemeMode()
        applyTheme(themeMode)
    }

    private fun isSystemDarkThemeEnabled(): Boolean {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun applyTheme(themeMode: String) {
        val mode = when (themeMode) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

}
