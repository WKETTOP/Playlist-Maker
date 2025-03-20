package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TrackSearchHistory {
    fun getTrackSearchHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearTrackSearchHistory()
}
