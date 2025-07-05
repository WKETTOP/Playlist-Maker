package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.PlaylistEntity
import com.example.playlistmaker.library.data.db.PlaylistWithTracks
import com.example.playlistmaker.library.domain.model.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            title = playlist.title,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            createdAt = playlist.createdAt
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistId = playlistEntity.playlistId,
            title = playlistEntity.title,
            description = playlistEntity.description,
            coverImagePath = playlistEntity.coverImagePath,
            createdAt = playlistEntity.createdAt,
            trackCount = 0
        )
    }

    fun map(playlistWithTracks: PlaylistWithTracks): Playlist {
        return Playlist(
            playlistId = playlistWithTracks.playlistEntity.playlistId,
            title = playlistWithTracks.playlistEntity.title,
            description = playlistWithTracks.playlistEntity.description,
            coverImagePath = playlistWithTracks.playlistEntity.coverImagePath,
            trackCount = playlistWithTracks.tracks.size,
            createdAt = playlistWithTracks.playlistEntity.createdAt
        )
    }
}