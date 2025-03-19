package com.example.playlistmaker.data

import android.content.Context
import com.example.playlistmaker.domain.api.AppSettingsRepository
import com.example.playlistmaker.ui.App
import androidx.core.content.edit

class AppSettingsRepositoryImpl(private val context: Context) : AppSettingsRepository {
    private val prefs by lazy {
        context.getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun getThemeMode(): String {
        return prefs.getString(App.THEME_MODE_KEY, App.FOLLOW_SYSTEM_MODE_KEY)!!
    }

    override fun saveThemeMode(themeMode: String) {
        prefs.edit { putString(App.THEME_MODE_KEY, themeMode) }
    }

    override fun applyTheme() {
        val currentTheme = getThemeMode()
        val app = context.applicationContext as App
        app.switchTheme(currentTheme)
    }
}
