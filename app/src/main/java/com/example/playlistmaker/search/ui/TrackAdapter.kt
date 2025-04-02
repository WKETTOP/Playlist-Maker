package com.example.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.TrackClickListener
import com.example.playlistmaker.search.domain.model.Track

class TrackAdapter(private val clickListener: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {

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
