package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TrackSearchHistory {
    suspend fun getTrackSearchHistory(): List<Track>
    suspend fun saveTrack(track: Track)
    fun clearTrackSearchHistory()
}
