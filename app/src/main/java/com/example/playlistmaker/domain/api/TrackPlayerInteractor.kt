package com.example.playlistmaker.domain.api

interface TrackPlayerInteractor {
    fun prepareTrack(url: String, onPrepared: () -> Unit)
    fun startPlayback()
    fun pausePlayback()
    fun releasePlayback()
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)

}
