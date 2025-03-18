package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.ThemeManager
import com.example.playlistmaker.ui.App

class ThemeManagerImpl(private val app: App) : ThemeManager {
    override fun getCurrentTheme(): String {
       return app.getCurrentTheme()
    }

    override fun switchTheme(themeMode: String) {
        app.switchTheme(themeMode)
    }
}
