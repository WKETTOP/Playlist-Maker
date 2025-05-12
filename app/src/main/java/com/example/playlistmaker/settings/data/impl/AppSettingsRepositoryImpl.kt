package com.example.playlistmaker.settings.data.impl

import android.content.Context
import com.example.playlistmaker.settings.domain.AppSettingsRepository
import androidx.core.content.edit

class AppSettingsRepositoryImpl(context: Context) : AppSettingsRepository {

    private val appContext = context.applicationContext
    private val prefs by lazy {
        appContext.getSharedPreferences(AppSettingsKeys.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    override fun getThemeMode(): String {
        return prefs.getString(AppSettingsKeys.THEME_MODE_KEY, AppSettingsKeys.FOLLOW_SYSTEM_MODE_KEY) ?: AppSettingsKeys.FOLLOW_SYSTEM_MODE_KEY
    }

    override fun saveThemeMode(themeMode: String) {
        prefs.edit { putString(AppSettingsKeys.THEME_MODE_KEY, themeMode) }
    }
}
