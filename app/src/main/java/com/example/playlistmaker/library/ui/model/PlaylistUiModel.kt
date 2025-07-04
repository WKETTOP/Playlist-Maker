package com.example.playlistmaker.library.ui.model

import android.net.Uri

data class PlaylistUiModel(
    val playlistId: Int,
    val title: String,
    val description: String?,
    val coverUri: Uri?,
    val trackCount: Int,
    val createdAt: Long
)