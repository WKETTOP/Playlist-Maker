package com.example.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks_table")
data class PlaylistTrackEntity(
    @PrimaryKey
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val formattedTrackTime: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String,
    val collectionName: String = "",
    val formattedReleaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
)
