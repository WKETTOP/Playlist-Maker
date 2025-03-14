package com.example.playlistmaker.domain.impl

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TrackMapper
import java.util.Locale

class TrackMapperImpl : TrackMapper {

    override fun map(trackDto: TrackDto): Track {
        return Track(
            trackId = trackDto.trackId,
            trackName = trackDto.trackName,
            artistName = trackDto.artistName,
            formattedTrackTime = millisToMin(trackDto.trackTimeMillis),
            artworkUrl100 = trackDto.artworkUrl100,
            collectionName = trackDto.collectionName,
            releaseDate = trackDto.releaseDate,
            primaryGenreName = trackDto.primaryGenreName,
            country = trackDto.country,
            previewUrl = trackDto.previewUrl
        )
    }

    private fun millisToMin(millis: String): String {
        return try {
            val seconds = (millis.toInt() / 1000)
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(seconds * 1000L)
        } catch (e: NumberFormatException) {
            "00:00"
        }
    }
}
