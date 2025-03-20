package com.example.playlistmaker.presentation

import com.example.playlistmaker.domain.models.Track

interface TrackClickListener {
    fun onTrackClicked(track: Track)
}
