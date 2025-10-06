package com.example.vitatrack

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment() {

    private lateinit var recentHabitsContainer: LinearLayout
    private lateinit var greetingText: TextView
    private lateinit var chartView: MoodChartView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recentHabitsContainer = view.findViewById(R.id.recentHabitsContainer)
        greetingText = view.findViewById(R.id.greetingText)
        chartView = view.findViewById(R.id.moodChartView)

        loadHomeData()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadHomeData() // 🔄 Refresh every time user comes back
    }

    private fun loadHomeData() {
        loadGreeting()
        loadHabits()
        loadMoodData()
    }

    // 👋 Show greeting based on saved username
    private fun loadGreeting() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Guest")
        greetingText.text = "Welcome back, $username 👋"
    }

    // ✅ Load recent habits from SharedPreferences
    private fun loadHabits() {
        val habitPrefs = requireContext().getSharedPreferences("habits_data", Context.MODE_PRIVATE)
        val json = habitPrefs.getString("habit_list", null)

        val habitList: MutableList<Habit> = if (json != null) {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            Gson().fromJson(json, type)
        } else mutableListOf()

        recentHabitsContainer.removeAllViews()

        if (habitList.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "No habits added yet"
                textSize = 16f
                setPadding(16, 8, 16, 8)
            }
            recentHabitsContainer.addView(emptyText)
        } else {
            habitList.take(3).forEach { habit ->
                val habitText = TextView(requireContext()).apply {
                    text = "• ${habit.name}  (🕒 ${habit.targetTime})"
                    textSize = 16f
                    setPadding(12, 8, 12, 8)
                }
                recentHabitsContainer.addView(habitText)
            }
        }
    }

    // 📊 Load and display mood statistics on chart
    private fun loadMoodData() {
        val prefs = requireContext().getSharedPreferences("mood_data", Context.MODE_PRIVATE)
        val json = prefs.getString("mood_list", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            val savedList: MutableList<MoodEntry> = Gson().fromJson(json, type)
            val grouped = savedList.groupingBy { it.emoji }.eachCount()

            chartView.setMoodData(grouped)
        } else {
            chartView.setMoodData(emptyMap()) // show placeholder
        }
    }
}
