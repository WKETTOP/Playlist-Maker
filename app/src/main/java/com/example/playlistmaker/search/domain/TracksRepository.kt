package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource

interface TracksRepository {
    fun searchTrack(expression: String): Resource<List<Track>>
}
