package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val trackSearchHistory: TrackSearchHistory
) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTrack(
        expression: String,
        consumer: TracksInteractor.TracksConsumer,
    ) {
        executor.execute {
            try {
                when (val resource = repository.searchTrack(expression)) {
                    is Resource.Success -> { consumer.consume(resource.data, null) }
                    is Resource.Error -> {
                        val message = resource.message.takeIf { !it.isNullOrEmpty() } ?: "Unknown error"
                        consumer.consume(null, message)
                    }
                }
            } catch (e: Throwable) {
                val errorMessage = e.message ?: "Failed to execute search"
                consumer.consume(null, errorMessage)
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
