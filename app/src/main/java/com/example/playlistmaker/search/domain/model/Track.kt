package com.example.playlistmaker.search.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val formattedTrackTime: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String,
    val formattedReleaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean = false
) : Parcelable {

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
}
