package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.PlaylistEntity
import com.example.playlistmaker.library.domain.model.Playlist
import com.google.gson.Gson

class PlaylistDbConverter(private val gson: Gson) {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            title = playlist.title,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount,
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        val trackIds = if (playlistEntity.trackIds.isNotEmpty()) {
            gson.fromJson(playlistEntity.trackIds, Array<String>::class.java).toList()
        } else {
            emptyList()
        }
        return Playlist(
            playlistId = playlistEntity.playlistId,
            title = playlistEntity.title,
            description = playlistEntity.description,
            coverImagePath = playlistEntity.coverImagePath,
            trackIds = trackIds,
            trackCount = playlistEntity.trackCount
        )
    }
}