package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.domain.model.Track

interface TracksInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer)
    fun getTrackSearchHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearTrackSearchHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}
