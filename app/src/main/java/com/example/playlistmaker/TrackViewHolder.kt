package com.example.playlistmaker

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class TrackViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
    ) {

    private val artWork: ImageView = itemView.findViewById(R.id.art_work)
    private val nameTrack: TextView = itemView.findViewById(R.id.track_name)
    private val nameArtist: TextView = itemView.findViewById(R.id.artist_name)
    private val timeTrack: TextView = itemView.findViewById(R.id.track_time)

    fun bind(track: Track) {

        val cornerRadiusInDp = 2f
        val cornerRadiusInPx = dpToPx(cornerRadiusInDp, itemView.context)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(cornerRadiusInPx))
            .into(artWork)
        nameTrack.text = track.trackName
        nameArtist.text = track.artistName
        timeTrack.text = millisToMin(track.trackTimeMillis)
        nameArtist.requestLayout()
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    private fun millisToMin(millis: String): String {
        val seconds = (millis.toInt() / 1000)
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(seconds * 1000L)
    }
}
