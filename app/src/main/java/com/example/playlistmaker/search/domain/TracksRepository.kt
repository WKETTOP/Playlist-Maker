package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTrack(expression: String): Flow<Resource<List<Track>>>
}
