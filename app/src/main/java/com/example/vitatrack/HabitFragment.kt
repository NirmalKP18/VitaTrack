package com.example.vitatrack

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HabitFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddHabit: MaterialButton
    private lateinit var adapter: HabitAdapter
    private val habits = mutableListOf<Habit>()
    private val prefsName = "habits_data"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit, container, false)

        recyclerView = view.findViewById(R.id.recyclerHabits)
        progressBar = view.findViewById(R.id.progressBar)
        btnAddHabit = view.findViewById(R.id.btnAddHabit)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HabitAdapter(habits, ::deleteHabit, ::toggleHabit)
        recyclerView.adapter = adapter

        loadHabits()

        btnAddHabit.setOnClickListener {
            showAddHabitDialog()
        }

        return view
    }

    private fun showAddHabitDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter habit name"

        AlertDialog.Builder(requireContext())
            .setTitle("New Habit")
            .setView(editText)
            .setPositiveButton("Add") { _, _ ->
                val habitName = editText.text.toString().trim()
                if (habitName.isNotEmpty()) {
                    habits.add(Habit(habitName))
                    saveHabits()
                    updateProgress()
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Enter a valid name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun toggleHabit(position: Int, isChecked: Boolean) {
        habits[position].isCompleted = isChecked
        saveHabits()
        updateProgress()
    }

    private fun deleteHabit(position: Int) {
        habits.removeAt(position)
        saveHabits()
        updateProgress()
        adapter.notifyDataSetChanged()
    }

    private fun updateProgress() {
        if (habits.isEmpty()) {
            progressBar.progress = 0
            return
        }
        val completed = habits.count { it.isCompleted }
        val progress = (completed * 100) / habits.size
        progressBar.progress = progress
    }

    private fun saveHabits() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(habits)
        editor.putString("habit_list", json)
        editor.apply()
    }

    private fun loadHabits() {
        val prefs = requireContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val json = prefs.getString("habit_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            val savedList: MutableList<Habit> = Gson().fromJson(json, type)
            habits.clear()
            habits.addAll(savedList)
            adapter.notifyDataSetChanged()
            updateProgress()
        }
    }
}
