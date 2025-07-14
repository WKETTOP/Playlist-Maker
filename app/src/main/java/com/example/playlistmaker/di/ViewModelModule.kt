package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.CreatePlaylistViewModel
import com.example.playlistmaker.library.ui.EditPlaylistViewModel
import com.example.playlistmaker.library.ui.FavoriteTracksViewModel
import com.example.playlistmaker.library.ui.OnePlaylistViewModel
import com.example.playlistmaker.library.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.TrackViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchTrackViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { (track: Track) ->
        TrackViewModel(track, get(), get(), get())
    }

    viewModel {
        SearchTrackViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        OnePlaylistViewModel(androidApplication(), get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }
}
