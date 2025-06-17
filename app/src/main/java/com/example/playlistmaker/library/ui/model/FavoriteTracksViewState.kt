package com.example.playlistmaker.library.ui.model

import com.example.playlistmaker.search.domain.model.Track

sealed interface FavoriteTracksViewState {

    data object Empty : FavoriteTracksViewState

    data class Content(val tracks: List<Track>) : FavoriteTracksViewState
}
