package com.example.playlistmaker.library.data.converters

import com.example.playlistmaker.library.data.db.FavoriteTrackEntity
import com.example.playlistmaker.search.domain.model.Track

class FavoriteTrackDbConverter {

    fun map(track: Track): FavoriteTrackEntity {
        return FavoriteTrackEntity(
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

    fun map(favoriteTrackEntity: FavoriteTrackEntity): Track {
        return Track(
            favoriteTrackEntity.trackId,
            favoriteTrackEntity.trackName,
            favoriteTrackEntity.artistName,
            favoriteTrackEntity.formattedTrackTime,
            favoriteTrackEntity.trackTimeMillis,
            favoriteTrackEntity.artworkUrl100,
            favoriteTrackEntity.collectionName,
            favoriteTrackEntity.formattedReleaseDate,
            favoriteTrackEntity.primaryGenreName,
            favoriteTrackEntity.country,
            favoriteTrackEntity.previewUrl,
            isFavorite = true
        )
    }
}