package com.example.playlistmaker.domain.impl

import android.icu.text.SimpleDateFormat
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TrackMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TrackMapperImpl : TrackMapper {

    override fun map(trackDto: TrackDto): Track {
        return Track(
            trackId = trackDto.trackId,
            trackName = trackDto.trackName,
            artistName = trackDto.artistName,
            formattedTrackTime = Transform.millisToMin(trackDto.trackTimeMillis),
            artworkUrl100 = trackDto.artworkUrl100,
            collectionName = trackDto.collectionName,
            formattedReleaseDate = Transform.dateToYear(trackDto.releaseDate),
            primaryGenreName = trackDto.primaryGenreName,
            country = trackDto.country,
            previewUrl = trackDto.previewUrl
        )
    }

}
