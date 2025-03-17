package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTrack(expression: String, consumer: TracksConsumer, errorConsumer: ErrorConsumer)
    fun getTrackSearchHistory(): List<Track>
    fun saveTrack(track: Track)
    fun clearTrackSearchHistory()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>)
    }

    interface ErrorConsumer {
        fun consume(errorMessage: String)
    }
}
