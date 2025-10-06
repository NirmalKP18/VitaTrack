package com.example.vitatrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitAdapter(
    private val habits: MutableList<Habit>,
    private val onDelete: (Int) -> Unit,
    private val onToggle: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    class HabitViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val habitName: TextView = view.findViewById(R.id.habitName)
        val habitCheck: CheckBox = view.findViewById(R.id.habitCheck)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        holder.habitName.text = habit.name
        holder.habitCheck.isChecked = habit.isCompleted

        holder.habitCheck.setOnCheckedChangeListener { _, isChecked ->
            onToggle(position, isChecked)
        }

        holder.btnDelete.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount() = habits.size
}
