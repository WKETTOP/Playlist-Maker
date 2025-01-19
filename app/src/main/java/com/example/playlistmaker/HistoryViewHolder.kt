package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HistoryViewHolder(parent: ViewGroup) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.history_item_view, parent, false)
    ) {

    private val historyText: TextView = itemView.findViewById(R.id.history_item_text)

    fun bind(query: String, onItemClickListener: (String) -> Unit) {
        historyText.text = query
        itemView.setOnClickListener { onItemClickListener(query) }
    }

}