package com.example.playlistmaker.data

import android.media.MediaPlayer
import com.example.playlistmaker.domain.api.MediaPlayerManager

class MediaPlayerManagerImpl : MediaPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var onCompleteListener: (() -> Unit)? = null

    override fun prepare(url: String, onPrepared: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                onPrepared()
            }
            setOnCompletionListener {
                onCompleteListener?.invoke()
            }
        }
    }

    override fun start() {
        mediaPlayer?.start()
    }

    override fun pause() {
        mediaPlayer?.pause()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer?.currentPosition ?: 0
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompleteListener = listener
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}