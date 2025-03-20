package com.example.playlistmaker.domain.api

interface MediaPlayerManager {
    fun prepare(url: String, onPrepared: () -> Unit)
    fun start()
    fun pause()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
    fun release()
}
