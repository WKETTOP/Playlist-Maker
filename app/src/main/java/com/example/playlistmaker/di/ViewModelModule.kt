package com.example.playlistmaker.di

import com.example.playlistmaker.player.ui.TrackViewModel
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.ui.SearchTrackViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {(track: Track) ->
        TrackViewModel(get(), track)
    }

    viewModel {
        SearchTrackViewModel(get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }
}
