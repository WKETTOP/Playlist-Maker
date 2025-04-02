package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TracksRepository {
    fun searchTrack(expression: String): List<Track>
}
