package com.example.playlistmaker.library.ui.model

sealed interface PlaylistViewState {

    data object Empty : PlaylistViewState

    data class Content(val playlists: List<PlaylistUiModel>) : PlaylistViewState
}