package com.example.playlistmaker.search.data

import com.example.playlistmaker.library.data.AppDatabase
import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.TrackMapper
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper,
    private val database: AppDatabase
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Check your internet connection"))
            }
            200 -> {
                val tracksIdInFavorite = database.trackDao().getTracksId()
                emit(Resource.Success((response as TracksSearchResponse).results.map { trackDto ->
                    trackMapper.map(trackDto).apply {
                        isFavorite = trackId in tracksIdInFavorite
                    }
                }))
            }
            else -> {
                emit(Resource.Error("Server error"))
            }
        }

    }
}
