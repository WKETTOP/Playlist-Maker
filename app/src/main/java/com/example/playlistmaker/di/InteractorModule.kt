package com.example.playlistmaker.di

import com.example.playlistmaker.library.domain.dp.FavoriteTracksInteractor
import com.example.playlistmaker.library.domain.dp.PlaylistInteractor
import com.example.playlistmaker.library.domain.impl.FavoriteTracksInteractorImpl
import com.example.playlistmaker.library.domain.impl.PlaylistInteractorImpl
import com.example.playlistmaker.player.domain.TrackPlayerInteractor
import com.example.playlistmaker.player.domain.impl.TrackPlayerInteractorImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.settings.domain.ThemeManager
import com.example.playlistmaker.settings.domain.impl.ThemeManagerImpl
import com.example.playlistmaker.sharing.domain.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module {

    factory<TrackPlayerInteractor> {
        TrackPlayerInteractorImpl(get())
    }

    factory<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    factory<ThemeManager> {
        ThemeManagerImpl(get(), androidContext())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(get())
    }

    factory<FavoriteTracksInteractor> {
        FavoriteTracksInteractorImpl(get())
    }

    factory<PlaylistInteractor> {
        PlaylistInteractorImpl(get())
    }
}
