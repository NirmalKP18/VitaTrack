package com.example.vitatrack

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vitatrack.data.Habit
import com.example.vitatrack.viewmodel.HabitViewModel
import com.google.android.material.button.MaterialButton
import java.util.*

class HabitFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAddHabit: MaterialButton
    private lateinit var adapter: HabitAdapter
    private lateinit var progressText: TextView
    
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_habit, container, false)

        recyclerView = view.findViewById(R.id.recyclerHabits)
        progressBar = view.findViewById(R.id.progressBar)
        btnAddHabit = view.findViewById(R.id.btnAddHabit)
        progressText = view.findViewById(R.id.progressText)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HabitAdapter(mutableListOf(), ::deleteHabit, ::toggleHabit)
        recyclerView.adapter = adapter

        setupObservers()
        setupClickListeners()

        return view
    }
    
    private fun setupObservers() {
        // Observe today's habits
        viewModel.todayHabits.observe(viewLifecycleOwner, Observer { habits ->
            adapter.updateHabits(habits)
        })
        
        // Observe progress
        viewModel.progressPercentage.observe(viewLifecycleOwner, Observer { percentage ->
            progressBar.progress = percentage
            updateProgressText(percentage)
        })
        
        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            // You can show/hide loading indicator here
        })
        
        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        })
    }
    
    private fun setupClickListeners() {
        btnAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun updateProgressText(percentage: Int) {
        val completed = viewModel.todayCompletedCount.value ?: 0
        val total = viewModel.todayTotalCount.value ?: 0
        
        progressText.text = when {
            total == 0 -> "No habits added yet"
            percentage == 100 -> "🎉 All habits completed today! Great job!"
            percentage >= 60 -> "Almost there! Keep it up 💪 ($completed/$total)"
            percentage > 0 -> "Good start! You've completed $completed of $total"
            else -> "Let's start completing your habits today!"
        }
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
                    viewModel.addHabit(name, selectedTime)
                    Toast.makeText(requireContext(), "Habit added successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // --------------------------------------------------
    //  Toggle habit status
    // --------------------------------------------------
    private fun toggleHabit(position: Int, isChecked: Boolean) {
        val habit = adapter.getHabitAt(position)
        if (habit != null) {
            viewModel.toggleHabitCompletion(habit)
        }
    }

    // --------------------------------------------------
    //  Delete habit
    // --------------------------------------------------
    private fun deleteHabit(position: Int) {
        val habit = adapter.getHabitAt(position)
        if (habit != null) {
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete '${habit.name}'?")
                .setPositiveButton("Delete") { _, _ ->
                    viewModel.deleteHabit(habit)
                    Toast.makeText(requireContext(), "Habit deleted", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
