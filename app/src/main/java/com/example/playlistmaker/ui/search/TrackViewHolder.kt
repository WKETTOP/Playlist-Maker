package com.example.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.impl.Transform
import com.example.playlistmaker.domain.models.Track

class TrackViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
    ) {

    private val artWork: ImageView = itemView.findViewById(R.id.art_work)
    private val nameTrack: TextView = itemView.findViewById(R.id.track_name)
    private val nameArtist: TextView = itemView.findViewById(R.id.artist_name)
    private val timeTrack: TextView = itemView.findViewById(R.id.track_time)

    fun bind(track: Track) {
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
    }

}
