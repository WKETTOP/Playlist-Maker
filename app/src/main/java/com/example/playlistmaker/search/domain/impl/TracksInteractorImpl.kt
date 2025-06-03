package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.TrackSearchHistory
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(
    private val repository: TracksRepository,
    private val trackSearchHistory: TrackSearchHistory
) : TracksInteractor {

    override fun searchTrack(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTrack(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
                }
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
