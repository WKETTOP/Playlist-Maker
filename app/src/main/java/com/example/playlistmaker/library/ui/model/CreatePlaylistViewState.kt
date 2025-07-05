package com.example.playlistmaker.library.ui.model

data class CreatePlaylistViewState(
    val playlistTitle: String = "",
    val playlistDescription: String = "",
    val coverImageUri: String? = null,
    val isCreateButtonEnabled: Boolean = false
)