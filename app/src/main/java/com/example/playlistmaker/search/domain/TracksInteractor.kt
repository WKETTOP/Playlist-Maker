package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>>
    fun getTrackSearchHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearTrackSearchHistory()
}
