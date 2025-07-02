package com.example.playlistmaker.library.ui.model

import android.net.Uri

data class CreatePlaylistViewState(
    val playlistTitle: String = "",
    val playlistDescription: String = "",
    val coverImageUri: Uri? = null,
    val isCreateButtonEnabled: Boolean = false
)