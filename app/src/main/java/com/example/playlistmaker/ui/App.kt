package com.example.playlistmaker.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val THEME_MODE_KEY = "theme_mode"
        const val FOLLOW_SYSTEM_MODE_KEY = "follow_system"
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val savedThemeMode = sharedPreferences.getString(THEME_MODE_KEY, null)

        if (savedThemeMode == null) {
            sharedPreferences.edit().putString(THEME_MODE_KEY, FOLLOW_SYSTEM_MODE_KEY).apply()
            switchTheme(FOLLOW_SYSTEM_MODE_KEY)
        } else {
            switchTheme(savedThemeMode)
        }
    }

    fun switchTheme(themeMode: String) {
        val sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        sharedPreferences.edit().putString(THEME_MODE_KEY, themeMode).apply()

        val mode = when (themeMode) {
            "dark" -> AppCompatDelegate.MODE_NIGHT_YES
            "light" -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }

        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun getCurrentTheme(): String {
        val sharedPreferences = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        return sharedPreferences.getString(THEME_MODE_KEY, "follow_system")!!
    }
}
