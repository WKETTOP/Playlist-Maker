package com.example.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val formattedTrackTime: String,
    val artworkUrl100: String,
    val collectionName: String = "",
    val formattedReleaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
)
