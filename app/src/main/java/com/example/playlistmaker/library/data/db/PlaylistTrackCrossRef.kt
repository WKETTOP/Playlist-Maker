package com.example.playlistmaker.library.data.db

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            parentColumns = ["playlistId"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlaylistTrackCrossRef(
    val playlistId: Int,
    val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)