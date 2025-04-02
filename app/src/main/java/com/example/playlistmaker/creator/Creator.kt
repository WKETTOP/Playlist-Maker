package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.settings.data.impl.AppSettingsRepositoryImpl
import com.example.playlistmaker.player.data.impl.MediaPlayerManagerImpl
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.dto.SharedPreferencesTrackSearchHistory
import com.example.playlistmaker.search.data.network.ItunesApi
import com.example.playlistmaker.search.data.network.RetrofitClient
import com.example.playlistmaker.search.data.network.TracksRepositoryImpl
import com.example.playlistmaker.settings.domain.AppSettingsRepository
import com.example.playlistmaker.settings.domain.ThemeManager
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.settings.domain.impl.ThemeManagerImpl
import com.example.playlistmaker.search.domain.impl.TrackMapperImpl
import com.example.playlistmaker.player.domain.impl.TrackPlayerInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
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

    private fun getAppSettingsRepository(): AppSettingsRepository {
        return AppSettingsRepositoryImpl(application)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(
            getTracksRepository(),
            getTrackSearchHistory()
        )
    }

    fun provideThemeManager(): ThemeManager {
        return ThemeManagerImpl(
            getAppSettingsRepository(),
            application
        )
    }

    fun provideTrackPlayer(): TrackPlayerInteractor {
        val mediaPlayerManager = MediaPlayerManagerImpl()
        return TrackPlayerInteractorImpl(mediaPlayerManager)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(ExternalNavigatorImpl(context))
    }
}
