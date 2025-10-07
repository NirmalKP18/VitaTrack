package com.example.vitatrack

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment() {

    private lateinit var recentHabitsContainer: LinearLayout
    private lateinit var greetingText: TextView
    private lateinit var todayProgressBar: ProgressBar
    private lateinit var todayProgressText: TextView
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
        todayProgressBar = view.findViewById(R.id.todayProgressBar)
        todayProgressText = view.findViewById(R.id.todayProgressText)

        loadHomeData()
        return view
    }

    override fun onResume() {
        super.onResume()
        loadHomeData() // Refresh every time you come back
    }

    private fun loadHomeData() {
        loadGreeting()
        loadHabits()
        loadProgress()
        loadMoodData()
    }

    // Greeting
    private fun loadGreeting() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Guest")
        greetingText.text = "Welcome back, $username 👋"
    }

    // Load recent habits
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

    // Calculate today's progress
    private fun loadProgress() {
        val prefs = requireContext().getSharedPreferences("habits_data", Context.MODE_PRIVATE)
        val json = prefs.getString("habit_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Habit>>() {}.type
            val habitList: MutableList<Habit> = Gson().fromJson(json, type)

            val total = habitList.size
            val completed = habitList.count { it.isCompleted }

            if (total == 0) {
                todayProgressBar.progress = 0
                todayProgressText.text = "No habits added yet"
            } else {
                val progress = (completed * 100) / total
                todayProgressBar.progress = progress

                todayProgressText.text = when {
                    progress == 100 -> "🎉 All habits completed today! Great job!"
                    progress >= 60 -> "Almost there! Keep it up 💪 ($completed/$total)"
                    progress > 0 -> "Good start! You’ve completed $completed of $total"
                    else -> "Let’s start completing your habits today!"
                }
            }
        } else {
            todayProgressBar.progress = 0
            todayProgressText.text = "No habits added yet"
        }
    }

    // Load mood data
    private fun loadMoodData() {
        val prefs = requireContext().getSharedPreferences("mood_data", Context.MODE_PRIVATE)
        val json = prefs.getString("mood_list", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<MoodEntry>>() {}.type
            val savedList: MutableList<MoodEntry> = Gson().fromJson(json, type)
            val grouped = savedList.groupingBy { it.emoji }.eachCount()
            chartView.setMoodData(grouped)
        } else {
            chartView.setMoodData(emptyMap())
        }
    }
}
