package com.example.playlistmaker.data.dto

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.domain.api.TrackSearchHistory
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.App
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesTrackSearchHistory(private val context: Context) : TrackSearchHistory {

    companion object {
        const val HISTORY_KEY = "track_search_history"
        private const val TRACK_SEARCH_MAX_SIZE = 10
    }

    override fun getTrackSearchHistory(): ArrayList<Track> {
        val prefs = context.getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        val json = prefs.getString(HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }


    override fun saveTrack(track: Track) {
        val historyTrack = getTrackSearchHistory()

        historyTrack.removeAll { it.trackId == track.trackId }
        historyTrack.add(0, track)

        if (historyTrack.size > TRACK_SEARCH_MAX_SIZE) {
            historyTrack.removeAt(historyTrack.size - 1)
        }

        saveTrackSearchHistory(historyTrack)
    }

    private fun saveTrackSearchHistory(trackHistory: ArrayList<Track>) {
        val prefs = context.getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        val json = Gson().toJson(trackHistory)
        prefs.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    override fun clearTrackSearchHistory() {
        val prefs = context.getSharedPreferences(App.PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(HISTORY_KEY)
            .apply()
    }
}
