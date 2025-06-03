package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.TrackMapper
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TracksRepository {

    override fun searchTrack(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Check your internet connection"))
            }
            200 -> {
                emit(Resource.Success((response as TracksSearchResponse).results.map { trackDto ->
                    trackMapper.map(trackDto)
                }))
            }
            else -> {
                emit(Resource.Error("Server error"))
            }
        }

    }
}
