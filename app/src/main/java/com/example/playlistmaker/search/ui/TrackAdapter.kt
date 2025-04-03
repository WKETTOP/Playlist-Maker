package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.playlistmaker.R
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.search.domain.model.Track

class TrackAdapter(private val onClick: (Track) -> Unit) : RecyclerView.Adapter<TrackViewHolder>() {

    private val tracks = mutableListOf<Track>()

    fun updateData(newItems: List<Track>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = tracks.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                tracks[oldItemPosition].trackId == newItems[newItemPosition].trackId

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                tracks[oldItemPosition] == newItems[newItemPosition]

        })
        tracks.clear()
        tracks.addAll(newItems)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.track_view, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position], onClick)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }
}
