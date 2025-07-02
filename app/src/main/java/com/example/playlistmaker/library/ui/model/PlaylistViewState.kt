package com.example.playlistmaker.library.ui.model

import com.example.playlistmaker.library.domain.model.Playlist

sealed interface PlaylistViewState {

    data object Empty : PlaylistViewState

    data class Content(val playlists: List<Playlist>) : PlaylistViewState
}