package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.library.data.PlaylistRepositoryImpl
import com.example.playlistmaker.library.data.converters.PlaylistDbConverter
import com.example.playlistmaker.library.data.converters.TrackDbConverter
import com.example.playlistmaker.library.domain.dp.FavoriteTracksRepository
import com.example.playlistmaker.library.domain.dp.PlaylistRepository
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

    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get(), get())
    }

    factory<AppSettingsRepository> {
        AppSettingsRepositoryImpl(androidContext())
    }

    factory<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(get(), get())
    }

    factory<PlaylistRepository> {
        PlaylistRepositoryImpl(
            get(),
            get(),
            get(),
            get(),
            androidContext()
        )
    }

    factory {
        TrackDbConverter()
    }

    factory {
        PlaylistDbConverter()
    }
}
