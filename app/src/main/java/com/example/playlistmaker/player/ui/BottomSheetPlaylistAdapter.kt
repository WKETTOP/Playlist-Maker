package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.model.Playlist

class BottomSheetPlaylistAdapter(private val onPlaylistClick: (Playlist) -> Unit) : RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {

    private var playlists = mutableListOf<Playlist>()

    fun updateData(newItems: List<Playlist>) {
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {

            override fun getOldListSize(): Int = playlists.size

            override fun getNewListSize(): Int = newItems.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                playlists[oldItemPosition].playlistId == newItems[newItemPosition].playlistId

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                playlists[oldItemPosition] == newItems[newItemPosition]
        })
        playlists.clear()
        playlists.addAll(newItems)
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistViewHolder {
        return BottomSheetPlaylistViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.playlist_view, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: BottomSheetPlaylistViewHolder,
        position: Int
    ) {
        holder.bind(playlists[position], onPlaylistClick)
    }

    override fun getItemCount(): Int = playlists.size

}