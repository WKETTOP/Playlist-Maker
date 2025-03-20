package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.AppSettingsRepositoryImpl
import com.example.playlistmaker.data.MediaPlayerManagerImpl
import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.SharedPreferencesTrackSearchHistory
import com.example.playlistmaker.data.network.ItunesApi
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.AppSettingsRepository
import com.example.playlistmaker.domain.api.ThemeManager
import com.example.playlistmaker.domain.api.TrackPlayerInteractor
import com.example.playlistmaker.domain.api.TrackSearchHistory
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.ThemeManagerImpl
import com.example.playlistmaker.domain.impl.TrackMapperImpl
import com.example.playlistmaker.domain.impl.TrackPlayerInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.ui.App
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getItunesApi(): ItunesApi {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ItunesApi::class.java)
    }

    private fun getNetworkClient(): NetworkClient {
        return RetrofitClient(getItunesApi())
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(getNetworkClient(), TrackMapperImpl())
    }

    private fun getSharedPreference(): SharedPreferences {
        return application.getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getTrackSearchHistory(): TrackSearchHistory {
        val prefs = getSharedPreference()
        return SharedPreferencesTrackSearchHistory(prefs)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(
            getTracksRepository(),
            getTrackSearchHistory()
        )
    }

    fun provideAppSettingsRepository(): AppSettingsRepository {
        return AppSettingsRepositoryImpl(application)
    }

    fun provideThemeManager(): ThemeManager {
        return ThemeManagerImpl(provideAppSettingsRepository())
    }

    fun provideTrackPlayer(): TrackPlayerInteractor {
        val mediaPlayerManager = MediaPlayerManagerImpl()
        return TrackPlayerInteractorImpl(mediaPlayerManager)
    }
}
