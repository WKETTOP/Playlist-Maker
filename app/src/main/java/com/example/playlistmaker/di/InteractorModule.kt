package com.example.playlistmaker.di

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

    single<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    single<ThemeManager> {
        ThemeManagerImpl(get(), androidContext())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get())
    }
}
