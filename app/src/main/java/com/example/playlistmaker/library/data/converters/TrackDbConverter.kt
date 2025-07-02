package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.PlaylistTrackEntity
import com.example.playlistmaker.library.data.db.TrackEntity
import com.example.playlistmaker.search.domain.model.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.formattedTrackTime,
            track.artworkUrl100,
            track.collectionName,
            track.formattedReleaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }

    fun map(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.formattedTrackTime,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.formattedReleaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            isFavorite = true
        )
    }

    fun mapToPlaylistTrack(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.formattedTrackTime,
            track.artworkUrl100,
            track.collectionName,
            track.formattedReleaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl,
        )
    }

    fun mapFromPlaylistTrack(playlistTrackEntity: PlaylistTrackEntity): Track {
        return Track(
            playlistTrackEntity.trackId,
            playlistTrackEntity.trackName,
            playlistTrackEntity.artistName,
            playlistTrackEntity.formattedTrackTime,
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
