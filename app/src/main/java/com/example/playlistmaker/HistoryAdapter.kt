package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(
    private val historyList: List<String>,
    private val onItemClickListener: (String) -> Unit
) : RecyclerView.Adapter<HistoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(parent)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position], onItemClickListener)
    }

    override fun getItemCount(): Int = historyList.size
}
