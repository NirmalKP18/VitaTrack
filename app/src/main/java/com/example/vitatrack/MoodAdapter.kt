package com.example.vitatrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MoodAdapter(private val moodList: List<MoodEntry>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textEmoji: TextView = itemView.findViewById(R.id.textEmoji)
        val textNote: TextView = itemView.findViewById(R.id.textNote)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val mood = moodList[position]
        holder.textEmoji.text = mood.emoji
        holder.textNote.text = if (mood.note.isNotEmpty()) mood.note else "No notes"
        holder.textDate.text = mood.date
    }

    override fun getItemCount(): Int = moodList.size
}
