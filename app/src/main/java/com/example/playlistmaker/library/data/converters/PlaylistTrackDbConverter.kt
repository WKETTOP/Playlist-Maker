package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.PlaylistTrackEntity
import com.example.playlistmaker.search.domain.model.Track

class PlaylistTrackDbConverter {

    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.formattedTrackTime,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.formattedReleaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }

    fun map(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            playlistTrackEntity.trackId,
            playlistTrackEntity.trackName,
            playlistTrackEntity.artistName,
            playlistTrackEntity.formattedTrackTime,
            playlistTrackEntity.trackTimeMillis,
            playlistTrackEntity.artworkUrl100,
            playlistTrackEntity.collectionName,
            playlistTrackEntity.formattedReleaseDate,
            playlistTrackEntity.primaryGenreName,
            playlistTrackEntity.country,
            playlistTrackEntity.previewUrl,
            isFavorite = false
        )
    }
}