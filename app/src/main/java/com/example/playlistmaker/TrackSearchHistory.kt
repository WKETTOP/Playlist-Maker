package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TrackSearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val HISTORY_KEY = "track_search_history"
        private const val TRACK_SEARCH_MAX_SIZE = 10
    }

    fun getTrackSearchHistory(): ArrayList<Track> {
        val json = sharedPreferences.getString(HISTORY_KEY, null) ?: return ArrayList()
        val type = object : TypeToken<ArrayList<Track>>() {}.type
        return Gson().fromJson(json, type) ?: ArrayList()
    }


    fun saveTrack(track: Track) {
        val historyTrack = getTrackSearchHistory()

        historyTrack.removeAll { it.trackId == track.trackId }
        historyTrack.add(0, track)

        if (historyTrack.size > TRACK_SEARCH_MAX_SIZE) {
            historyTrack.removeAt(historyTrack.size - 1)
        }

        saveTrackSearchHistory(historyTrack)
    }

    private fun saveTrackSearchHistory(trackHistory: ArrayList<Track>) {
        val json = Gson().toJson(trackHistory)
        sharedPreferences.edit()
            .putString(HISTORY_KEY, json)
            .apply()
    }

    fun clearTrackSearchHistory() {
        sharedPreferences.edit()
            .remove(HISTORY_KEY)
            .apply()
    }
}