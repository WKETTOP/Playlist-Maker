package com.example.playlistmaker.ui.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.presentation.TrackClickListener
import com.example.playlistmaker.data.dto.SharedPreferencesTrackSearchHistory
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(
    private val searchHistory: SharedPreferencesTrackSearchHistory,
    private val clickListener: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder>() {

    var tracks = ArrayList<Track>()
    private val observers = mutableListOf<TrackClickListener>()

    fun addObserver(observer: TrackClickListener) {
        observers.add(observer)
    }

    fun removeObserver(observer: TrackClickListener) {
        observers.remove(observer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            searchHistory.saveTrack(tracks[position])
            clickListener(tracks[position])
        }
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    fun updateData(newItems: ArrayList<Track>) {
        tracks.clear()
        tracks.addAll(newItems)
        notifyDataSetChanged()
    }
}
