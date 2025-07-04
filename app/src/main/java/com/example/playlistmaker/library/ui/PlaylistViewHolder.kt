package com.example.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.model.PlaylistUiModel
import com.example.playlistmaker.util.Transform

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val playlistTitle: TextView = itemView.findViewById(R.id.playlist_title)
    private val playlistTracksCount: TextView = itemView.findViewById(R.id.playlist_tracks_count)

    fun bind(playlist: PlaylistUiModel, onClick: (PlaylistUiModel) -> Unit) {
        val cornerRadius = Transform.dpToPx(8f, itemView.context)

        Glide.with(itemView)
            .load(playlist.coverUri)
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
        playlistTracksCount.text = trackCountText

        itemView.setOnClickListener { onClick(playlist) }
    }
}