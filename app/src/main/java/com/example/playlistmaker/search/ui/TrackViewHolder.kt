package com.example.playlistmaker.search.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.util.Transform

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val artWork: ImageView = itemView.findViewById(R.id.art_work)
    private val nameTrack: TextView = itemView.findViewById(R.id.track_name)
    private val nameArtist: TextView = itemView.findViewById(R.id.artist_name)
    private val timeTrack: TextView = itemView.findViewById(R.id.track_time)

    fun bind(track: Track, onClick: (Track) -> Unit) {
        val cornerRadius = Transform.dpToPx(2f, itemView.context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadius))
            .into(artWork)
        nameTrack.text = track.trackName
        nameArtist.text = track.artistName
        timeTrack.text = track.formattedTrackTime
        nameArtist.requestLayout()

        itemView.setOnClickListener { onClick(track) }
    }

}
