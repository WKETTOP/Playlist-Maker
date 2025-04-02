package com.example.playlistmaker.creator

import android.app.Application

class App : Application() {

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
    }

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)
        Creator.provideThemeManager().applySavedTheme()
    }
}
