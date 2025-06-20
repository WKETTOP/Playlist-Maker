package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>
    suspend fun getTrackSearchHistory(): List<Track>
    suspend fun saveTrack(track: Track)
    fun clearTrackSearchHistory()
}
