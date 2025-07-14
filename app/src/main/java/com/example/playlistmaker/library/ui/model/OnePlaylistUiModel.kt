package com.example.playlistmaker.library.ui.model

import android.net.Uri
import android.os.Parcelable
import com.example.playlistmaker.search.domain.model.Track
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnePlaylistUiModel(
    val playlistId: Int,
    val title: String,
    val description: String,
    val coverUri: Uri?,
    val trackCount: String,
    val totalDuration: String,
    val tracks: List<Track>,
    val createAt: Long
) : Parcelable