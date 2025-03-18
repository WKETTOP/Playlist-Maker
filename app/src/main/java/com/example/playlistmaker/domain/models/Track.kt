package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val formattedTrackTime: String,
    val artworkUrl100: String,
    val collectionName: String,
    val formattedReleaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
) : Parcelable {

    fun getCoverArtWork() = artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")

}
