package com.example.playlistmaker.search.domain.model

import com.example.playlistmaker.search.data.dto.TrackDto

interface TrackMapper {
    fun map(trackDto: TrackDto): Track
}
