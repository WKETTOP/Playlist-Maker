package com.example.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.Creator

class App : Application() {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val THEME_MODE_KEY = "theme_mode"
        const val FOLLOW_SYSTEM_MODE_KEY = "follow_system"
    }

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        val settingsRepository = Creator.provideAppSettingsRepository()
        val savedThemeMode = settingsRepository.getThemeMode()

        if (savedThemeMode == FOLLOW_SYSTEM_MODE_KEY) {
            switchTheme(FOLLOW_SYSTEM_MODE_KEY)
        } else {
            switchTheme(savedThemeMode)
        }
    }

    fun switchTheme(themeMode: String) {
        val mode = when (themeMode) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

}
