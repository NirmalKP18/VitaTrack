package com.example.vitatrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vitatrack.data.Habit

class HabitAdapter(
    private var habits: MutableList<Habit>,
    private val onDelete: (Int) -> Unit,
    private val onToggle: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitName: TextView = view.findViewById(R.id.habitName)
        val habitTime: TextView = view.findViewById(R.id.habitTime)
        val habitCheck: CheckBox = view.findViewById(R.id.habitCheck)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
        val streakText: TextView = view.findViewById(R.id.streakText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.habitName.text = habit.name
        holder.habitTime.text = habit.targetTime
        holder.habitCheck.isChecked = habit.isCompleted
        
        // Show streak information
        if (habit.streak > 0) {
            holder.streakText.text = "🔥 ${habit.streak} day streak"
            holder.streakText.visibility = View.VISIBLE
        } else {
            holder.streakText.visibility = View.GONE
        }

        holder.habitCheck.setOnCheckedChangeListener { _, isChecked ->
            onToggle(position, isChecked)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount() = habits.size
    
    fun updateHabits(newHabits: List<Habit>) {
        habits.clear()
        habits.addAll(newHabits)
        notifyDataSetChanged()
    }
    
    fun getHabitAt(position: Int): Habit? {
        return if (position >= 0 && position < habits.size) {
            habits[position]
        } else {
            null
        }
    }
}
