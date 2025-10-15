package com.example.vitatrack

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.vitatrack.data.Habit
import com.example.vitatrack.data.MoodEntry
import com.example.vitatrack.data.dao.MoodFrequency
import com.example.vitatrack.data.dao.MoodTrend
import com.example.vitatrack.data.dao.HydrationTrend
import com.example.vitatrack.viewmodel.HomeViewModel
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var recentHabitsContainer: LinearLayout
    private lateinit var greetingText: TextView
    private lateinit var todayProgressBar: ProgressBar
    private lateinit var todayProgressText: TextView
    private lateinit var moodPieChart: PieChart
    private lateinit var moodLineChart: LineChart
    private lateinit var hydrationBarChart: BarChart
    private lateinit var habitProgressText: TextView
    private lateinit var moodSummaryText: TextView
    private lateinit var hydrationSummaryText: TextView
    
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recentHabitsContainer = view.findViewById(R.id.recentHabitsContainer)
        greetingText = view.findViewById(R.id.greetingText)
        todayProgressBar = view.findViewById(R.id.todayProgressBar)
        todayProgressText = view.findViewById(R.id.todayProgressText)
        moodPieChart = view.findViewById(R.id.moodPieChart)
        moodLineChart = view.findViewById(R.id.moodLineChart)
        hydrationBarChart = view.findViewById(R.id.hydrationBarChart)
        habitProgressText = view.findViewById(R.id.habitProgressText)
        moodSummaryText = view.findViewById(R.id.moodSummaryText)
        hydrationSummaryText = view.findViewById(R.id.hydrationSummaryText)

        setupCharts()
        setupObservers()
        loadGreeting()

        return view
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData() // Refresh every time you come back
    }

    private fun setupCharts() {
        setupMoodPieChart()
        setupMoodLineChart()
        setupHydrationBarChart()
    }
    
    private fun setupMoodPieChart() {
        moodPieChart.description.isEnabled = false
        moodPieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        moodPieChart.dragDecelerationFrictionCoef = 0.95f
        moodPieChart.isRotationEnabled = true
        moodPieChart.isHighlightPerTapEnabled = true
        moodPieChart.setCenterText("Mood Distribution")
        moodPieChart.setCenterTextSize(12f)
        moodPieChart.setCenterTextColor(Color.BLACK)
        moodPieChart.legend.isEnabled = true
        moodPieChart.legend.textSize = 10f
    }
    
    private fun setupMoodLineChart() {
        moodLineChart.description.isEnabled = false
        moodLineChart.setTouchEnabled(true)
        moodLineChart.isDragEnabled = true
        moodLineChart.setScaleEnabled(true)
        moodLineChart.setPinchZoom(true)
        moodLineChart.setBackgroundColor(Color.WHITE)
        
        val xAxis = moodLineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        
        val leftAxis = moodLineChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 5f
        
        moodLineChart.axisRight.isEnabled = false
        moodLineChart.legend.isEnabled = true
    }
    
    private fun setupHydrationBarChart() {
        hydrationBarChart.description.isEnabled = false
        hydrationBarChart.setTouchEnabled(true)
        hydrationBarChart.isDragEnabled = true
        hydrationBarChart.setScaleEnabled(true)
        hydrationBarChart.setPinchZoom(true)
        hydrationBarChart.setBackgroundColor(Color.WHITE)
        
        val xAxis = hydrationBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        
        val leftAxis = hydrationBarChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.axisMinimum = 0f
        
        hydrationBarChart.axisRight.isEnabled = false
        hydrationBarChart.legend.isEnabled = true
    }
    
    private fun setupObservers() {
        // Observe today's habits
        viewModel.todayHabits.observe(viewLifecycleOwner, Observer { habits ->
            loadHabits(habits)
        })
        
        // Observe progress
        viewModel.progressPercentage.observe(viewLifecycleOwner, Observer { percentage ->
            todayProgressBar.progress = percentage
            updateProgressText(percentage)
        })
        
        // Observe mood frequency
        viewModel.moodFrequency.observe(viewLifecycleOwner, Observer { frequency ->
            updateMoodPieChart(frequency)
        })
        
        // Observe mood trend
        viewModel.moodTrend.observe(viewLifecycleOwner, Observer { trend ->
            updateMoodLineChart(trend)
        })
        
        // Observe hydration trend
        viewModel.hydrationTrend.observe(viewLifecycleOwner, Observer { trend ->
            updateHydrationBarChart(trend)
        })
        
        // Observe today's hydration
        viewModel.todayHydration.observe(viewLifecycleOwner, Observer { hydration ->
            updateHydrationSummary(hydration)
        })
        
        // Observe mood frequency for summary
        viewModel.moodFrequency.observe(viewLifecycleOwner, Observer { frequency ->
            updateMoodSummary(frequency)
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                // Handle error if needed
                viewModel.clearErrorMessage()
            }
        })
    }
    
    private fun loadGreeting() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = prefs.getString("username", "Guest")
        greetingText.text = "Welcome back, $username 👋"
    }
    
    private fun loadHabits(habits: List<Habit>) {
        recentHabitsContainer.removeAllViews()

        if (habits.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "No habits added yet"
                textSize = 16f
                setPadding(16, 8, 16, 8)
            }
            recentHabitsContainer.addView(emptyText)
        } else {
            habits.take(3).forEach { habit ->
                val habitText = TextView(requireContext()).apply {
                    text = "• ${habit.name}  (🕒 ${habit.targetTime})"
                    textSize = 16f
                    setPadding(12, 8, 12, 8)
                }
                recentHabitsContainer.addView(habitText)
            }
        }
    }
    
    private fun updateProgressText(percentage: Int) {
        val completed = viewModel.todayCompletedCount.value ?: 0
        val total = viewModel.todayTotalCount.value ?: 0
        
        todayProgressText.text = when {
            total == 0 -> "No habits added yet"
            percentage == 100 -> "🎉 All habits completed today! Great job!"
            percentage >= 60 -> "Almost there! Keep it up 💪 ($completed/$total)"
            percentage > 0 -> "Good start! You've completed $completed of $total"
            else -> "Let's start completing your habits today!"
        }
        
        habitProgressText.text = "Habit Progress: $percentage%"
    }
    
    private fun updateMoodPieChart(frequency: List<MoodFrequency>) {
        if (frequency.isEmpty()) {
            moodPieChart.clear()
            moodPieChart.invalidate()
            return
        }
        
        val entries = frequency.map { 
            PieEntry(it.count.toFloat(), it.emoji)
        }
        
        val dataSet = PieDataSet(entries, "Mood Frequency")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE
        
        val data = PieData(dataSet)
        data.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}"
            }
        })
        
        moodPieChart.data = data
        moodPieChart.invalidate()
    }
    
    private fun updateMoodLineChart(trend: List<MoodTrend>) {
        if (trend.isEmpty()) {
            moodLineChart.clear()
            moodLineChart.invalidate()
            return
        }
        
        val entries = trend.mapIndexed { index, trendItem ->
            Entry(index.toFloat(), trendItem.avgScore.toFloat())
        }
        
        val dataSet = LineDataSet(entries, "Mood Trend")
        dataSet.color = Color.parseColor("#06B5D2")
        dataSet.setCircleColor(Color.parseColor("#06B5D2"))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawCircleHole(false)
        dataSet.valueTextSize = 10f
        
        val data = LineData(dataSet)
        moodLineChart.data = data
        
        // Set X-axis labels
        val dates = trend.map { 
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)!!
            )
        }
        moodLineChart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        
        moodLineChart.invalidate()
    }
    
    private fun updateHydrationBarChart(trend: List<HydrationTrend>) {
        if (trend.isEmpty()) {
            hydrationBarChart.clear()
            hydrationBarChart.invalidate()
            return
        }
        
        val entries = trend.mapIndexed { index, trendItem ->
            BarEntry(index.toFloat(), trendItem.totalMl.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Daily Hydration (ml)")
        dataSet.color = Color.parseColor("#4CAF50")
        dataSet.valueTextSize = 10f
        
        val data = BarData(dataSet)
        hydrationBarChart.data = data
        
        // Set X-axis labels
        val dates = trend.map { 
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)!!
            )
        }
        hydrationBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(dates)
        
        hydrationBarChart.invalidate()
    }
    
    private fun updateHydrationSummary(hydration: Int) {
        val goal = 2000 // Default goal
        val percentage = if (goal > 0) ((hydration.toFloat() / goal) * 100).toInt() else 0
        
        // Update hydration summary text
        hydrationSummaryText.text = "Today: ${hydration}ml / ${goal}ml ($percentage%)"
    }
    
    private fun updateMoodSummary(frequency: List<MoodFrequency>) {
        if (frequency.isEmpty()) {
            moodSummaryText.text = "Mood: No entries today"
        } else {
            val mostFrequent = frequency.maxByOrNull { it.count }
            mostFrequent?.let {
                moodSummaryText.text = "Mood: Most frequent ${it.emoji} (${it.count} times)"
            }
        }
    }
}
