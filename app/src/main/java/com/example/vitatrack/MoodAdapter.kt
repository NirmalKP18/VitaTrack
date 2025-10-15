package com.example.vitatrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vitatrack.data.MoodEntry

class MoodAdapter(private var moodList: MutableList<MoodEntry>) :
    RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textEmoji: TextView = itemView.findViewById(R.id.textEmoji)
        val textNote: TextView = itemView.findViewById(R.id.textNote)
        val textDate: TextView = itemView.findViewById(R.id.textDate)
        val textTime: TextView = itemView.findViewById(R.id.textTime)
        val moodScoreText: TextView = itemView.findViewById(R.id.moodScoreText)
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
        holder.textTime.text = mood.time
        
        // Show mood score with stars
        val stars = "★".repeat(mood.moodScore) + "☆".repeat(5 - mood.moodScore)
        holder.moodScoreText.text = stars
    }

    override fun getItemCount(): Int = moodList.size
    
    fun updateMoodEntries(newMoodEntries: List<MoodEntry>) {
        moodList.clear()
        moodList.addAll(newMoodEntries)
        notifyDataSetChanged()
    }
    
    fun getMoodEntryAt(position: Int): MoodEntry? {
        return if (position >= 0 && position < moodList.size) {
            moodList[position]
        } else {
            null
        }
    }
}
