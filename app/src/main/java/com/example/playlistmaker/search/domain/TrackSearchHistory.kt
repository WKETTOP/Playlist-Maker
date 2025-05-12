package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TrackSearchHistory {
    fun getTrackSearchHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearTrackSearchHistory()
}
