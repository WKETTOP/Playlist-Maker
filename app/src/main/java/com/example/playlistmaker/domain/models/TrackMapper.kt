package com.example.playlistmaker.domain.models

import com.example.playlistmaker.data.dto.TrackDto

interface TrackMapper {
    fun map(trackDto: TrackDto): Track
}
