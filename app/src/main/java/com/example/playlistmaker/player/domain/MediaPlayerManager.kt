package com.example.playlistmaker.player.domain

interface MediaPlayerManager {
    fun prepare(url: String, onPrepared: () -> Unit)
    fun start()
    fun pause()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnCompletionListener(listener: () -> Unit)
    fun release()
}
