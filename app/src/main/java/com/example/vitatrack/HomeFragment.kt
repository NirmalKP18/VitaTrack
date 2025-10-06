package com.example.vitatrack

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HomeFragment : Fragment() {

    private lateinit var greetingText: TextView
    private lateinit var habitSummaryText: TextView
    private lateinit var moodChartView: MoodChartView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        greetingText = view.findViewById(R.id.greetingText)
        habitSummaryText = view.findViewById(R.id.habitSummaryText)
        moodChartView = view.findViewById(R.id.moodChartView)

        loadUserGreeting()
        loadHabitSummary()
        loadMoodChart()

        return view
    }

    private fun loadUserGreeting() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Guest")
        greetingText.text = "Welcome back, $username 👋"
    }

    private fun loadHabitSummary() {
        val prefs = requireContext().getSharedPreferences("habit_data", Context.MODE_PRIVATE)
        val json = prefs.getString("habit_list", null)
        val count = if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            Gson().fromJson<List<Habit>>(json, type).size
        } else 0
        habitSummaryText.text = "You’ve created $count habits so far 🏆"
    }

    private fun loadMoodChart() {
        val prefs = requireContext().getSharedPreferences("mood_data", Context.MODE_PRIVATE)
        val json = prefs.getString("mood_list", null)
        if (json.isNullOrEmpty()) {
            moodChartView.setMoodData(mapOf())
            return
        }

        val type = object : TypeToken<List<MoodEntry>>() {}.type
        val moods: List<MoodEntry> = Gson().fromJson(json, type)
        val moodCounts = moods.groupingBy { it.emoji }.eachCount()

        moodChartView.setMoodData(moodCounts)
    }
}
