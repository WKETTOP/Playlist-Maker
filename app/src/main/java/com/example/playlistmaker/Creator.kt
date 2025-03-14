package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.dto.SharedPreferencesTrackSearchHistory
import com.example.playlistmaker.data.network.RetrofitClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TrackSearchHistory
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.TrackMapperImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitClient(), TrackMapperImpl())
    }

    private fun getTrackSearchHistory(context: Context): TrackSearchHistory {
        return SharedPreferencesTrackSearchHistory(context)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(
            getTracksRepository(),
            getTrackSearchHistory(context)
        )
    }
}
