package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.MediaPlayerManager
import com.example.playlistmaker.domain.api.TrackPlayerInteractor

class TrackPlayerInteractorImpl(private val mediaPlayerManager: MediaPlayerManager) : TrackPlayerInteractor{
    override fun prepareTrack(url: String, onPrepared: () -> Unit) {
        mediaPlayerManager.prepare(url, onPrepared)
    }

    override fun startPlayback() {
        mediaPlayerManager.start()
    }

    override fun pausePlayback() {
        mediaPlayerManager.pause()
    }

    override fun releasePlayback() {
        mediaPlayerManager.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerManager.getCurrentPosition()
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        mediaPlayerManager.setOnCompletionListener(listener)
    }
}
