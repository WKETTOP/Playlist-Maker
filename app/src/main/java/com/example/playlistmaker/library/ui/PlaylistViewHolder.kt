package com.example.playlistmaker.library.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.util.Transform
import java.io.File

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistCover: ImageView = itemView.findViewById(R.id.playlist_cover)
    private val playlistTitle: TextView = itemView.findViewById(R.id.playlist_title)
    private val playlistTracksCount: TextView = itemView.findViewById(R.id.playlist_tracks_count)

    fun bind(playlist: Playlist, onClick: (Playlist) -> Unit) {
        val cornerRadius = Transform.dpToPx(8f, itemView.context)

        if (playlist.coverImagePath.isNotEmpty()) {
            val file = File(playlist.coverImagePath)
            if (file.exists()) {
                Glide.with(itemView)
                    .load(file)
                    .placeholder(R.drawable.placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(cornerRadius))
                    .into(playlistCover)
            } else {
                playlistCover.setImageResource(R.drawable.placeholder)
            }
        } else {
            playlistCover.setImageResource(R.drawable.placeholder)
        }

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