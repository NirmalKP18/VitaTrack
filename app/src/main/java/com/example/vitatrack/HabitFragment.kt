package com.example.vitatrack

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

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

    // --------------------------------------------------
    //  Show dialog to add a habit
    // --------------------------------------------------
    private fun showAddHabitDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_habit, null)

        val inputName = dialogView.findViewById<EditText>(R.id.inputHabitName)
        val btnPickTime = dialogView.findViewById<Button>(R.id.btnPickTime)
        val timeText = dialogView.findViewById<TextView>(R.id.selectedTime)

        var selectedTime = ""

        // Pick time
        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            TimePickerDialog(requireContext(), { _, h, m ->
                val formatted = String.format(
                    "%02d:%02d %s",
                    if (h % 12 == 0) 12 else h % 12,
                    m,
                    if (h < 12) "AM" else "PM"
                )
                selectedTime = formatted
                timeText.text = "Selected: $formatted"
            }, hour, minute, false).show()
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = inputName.text.toString().trim()
                if (name.isNotEmpty() && selectedTime.isNotEmpty()) {
                    addHabit(name, selectedTime)
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --------------------------------------------------
    //  Add habit + save + refresh home fragment
    // --------------------------------------------------
    private fun addHabit(name: String, time: String) {
        val newHabit = Habit(name = name, targetTime = time)
        habits.add(0, newHabit)
        saveHabits()

        adapter.notifyItemInserted(0)
        updateProgress()

        //  Refresh HomeFragment to show new habit immediately
        requireActivity().supportFragmentManager.fragments.forEach {
            if (it is HomeFragment) {
                it.onResume()
            }
        }
    }

    // --------------------------------------------------
    //  Toggle habit status
    // --------------------------------------------------
    private fun toggleHabit(position: Int, isChecked: Boolean) {
        habits[position].isCompleted = isChecked
        saveHabits()
        updateProgress()
    }

    // --------------------------------------------------
    //  Delete habit
    // --------------------------------------------------
    private fun deleteHabit(position: Int) {
        habits.removeAt(position)
        saveHabits()
        updateProgress()
        adapter.notifyDataSetChanged()
    }

    // --------------------------------------------------
    //  Progress Bar update
    // --------------------------------------------------
    private fun updateProgress() {
        if (habits.isEmpty()) {
            progressBar.progress = 0
            return
        }
        val completed = habits.count { it.isCompleted }
        val progress = (completed * 100) / habits.size
        progressBar.progress = progress
    }

    // --------------------------------------------------
    //  Save and Load
    // --------------------------------------------------
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
