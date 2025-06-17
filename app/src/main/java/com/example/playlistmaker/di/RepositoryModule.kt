package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.library.domain.dp.FavoriteTracksRepository
import com.example.playlistmaker.player.data.impl.MediaPlayerManagerImpl
import com.example.playlistmaker.player.domain.MediaPlayerManager
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.impl.TrackMapperImpl
import com.example.playlistmaker.search.domain.model.TrackMapper
import com.example.playlistmaker.settings.data.impl.AppSettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.AppSettingsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    factory<TrackMapper> {
        TrackMapperImpl()
    }

    factory<MediaPlayerManager> {
        MediaPlayerManagerImpl()
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    single<AppSettingsRepository> {
        AppSettingsRepositoryImpl(androidContext())
    }

    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }
}
