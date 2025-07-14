package com.example.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
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
    val addedAt: Long = System.currentTimeMillis()
)
