package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.TrackSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.TracksRepository
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.search.domain.model.TrackMapper
import com.example.playlistmaker.util.Resource

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : TracksRepository {

    override fun searchTrack(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Check your internet connection")
            }
            200 -> {
                Resource.Success((response as TracksSearchResponse).results.map { trackDto ->
                    trackMapper.map(trackDto)
                })
            }
            else -> {
                Resource.Error("Server error")
            }
        }

    }
}
