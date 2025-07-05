package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.util.Transform

class BottomSheetPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover_view)
    private val playlistTitle: TextView = itemView.findViewById(R.id.playlist_title_view)
    private val trackCount: TextView = itemView.findViewById(R.id.playlist_tracks_count_view)

    fun bind(playlist: Playlist, onPlaylistClick: (Playlist) -> Unit) {
        val cornerRadius = Transform.dpToPx(2f, itemView.context)

        Glide.with(itemView)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(playlistCover)

        playlistTitle.text = playlist.title

        val trackCountText = itemView.context.resources.getQuantityString(
            R.plurals.track_count,
            playlist.trackCount,
            playlist.trackCount
        )
        trackCount.text = trackCountText

        itemView.setOnClickListener { onPlaylistClick(playlist) }
    }
}