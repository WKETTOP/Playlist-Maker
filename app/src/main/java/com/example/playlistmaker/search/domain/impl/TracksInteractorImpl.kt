package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val trackSearchHistory: TrackSearchHistory
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(
        expression: String,
        consumer: TracksInteractor.TracksConsumer,
        errorConsumer: TracksInteractor.ErrorConsumer
    ) {
        executor.execute {
            try {
                consumer.consume(repository.searchTrack(expression))
            } catch (e: Exception) {
                errorConsumer.consume(e.message ?: "Unknown error")
            }
        }
    }

    override fun getTrackSearchHistory(): List<Track> {
        return trackSearchHistory.getTrackSearchHistory()
    }

    override fun saveTrack(track: Track) {
        trackSearchHistory.saveTrack(track)
    }

    override fun clearTrackSearchHistory() {
        trackSearchHistory.clearTrackSearchHistory()
    }
}
