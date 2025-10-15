package com.example.vitatrack

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.vitatrack.viewmodel.HydrationViewModel
import com.google.android.material.button.MaterialButton

class HydrationFragment : Fragment() {

    private lateinit var waterFillView: View
    private lateinit var textProgress: TextView
    private lateinit var btnAdd: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnSetReminder: MaterialButton
    private lateinit var btnCancelReminder: MaterialButton
    private lateinit var goalText: TextView
    private lateinit var progressPercentageText: TextView

    private val viewModel: HydrationViewModel by viewModels()

    companion object {
        private const val REQUEST_CODE_REMINDER = 4242
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_hydration, container, false)

        waterFillView = view.findViewById(R.id.waterFillView)
        textProgress = view.findViewById(R.id.textProgress)
        btnAdd = view.findViewById(R.id.btnAddGlass)
        btnReset = view.findViewById(R.id.btnReset)
        btnSetReminder = view.findViewById(R.id.btnSetReminder)
        btnCancelReminder = view.findViewById(R.id.btnCancelReminder)
        goalText = view.findViewById(R.id.goalText)
        progressPercentageText = view.findViewById(R.id.progressPercentageText)

        setupObservers()
        setupClickListeners()

        return view
    }
    
    private fun setupObservers() {
        // Observe today's total hydration
        viewModel.todayTotalHydration.observe(viewLifecycleOwner, Observer { total ->
            updateUI()
        })
        
        // Observe user settings for daily goal
        viewModel.userSettings.observe(viewLifecycleOwner, Observer { settings ->
            updateUI()
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
        btnAdd.setOnClickListener { addGlass() }
        btnReset.setOnClickListener { resetProgress() }
        btnSetReminder.setOnClickListener { scheduleHydrationReminderInOneMinute() }
        btnCancelReminder.setOnClickListener { cancelHydrationReminder() }
    }

    //  Add glass logic with animation
    private fun addGlass() {
        val total = viewModel.todayTotalHydration.value ?: 0
        val goal = viewModel.userSettings.value?.dailyWaterGoalMl ?: 2000
        
        if (total < goal) {
            viewModel.addGlass() // Add 250ml (standard glass)
            Toast.makeText(requireContext(), "Added 250ml! 💧", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Goal reached! 🎉", Toast.LENGTH_SHORT).show()
        }
    }

    //  Reset progress
    private fun resetProgress() {
        // Note: In a real app, you might want to ask for confirmation
        // For now, we'll just show a message since we don't have a delete all function
        Toast.makeText(requireContext(), "Reset functionality would delete all today's entries", Toast.LENGTH_LONG).show()
    }

    //  Update progress + animation
    private fun updateUI() {
        val total = viewModel.todayTotalHydration.value ?: 0
        val goal = viewModel.userSettings.value?.dailyWaterGoalMl ?: 2000
        
        // Convert ml to glasses (assuming 250ml per glass)
        val glasses = total / 250
        val goalGlasses = goal / 250
        
        textProgress.text = "${total}ml / ${goal}ml"
        goalText.text = "Goal: ${goalGlasses} glasses (${goal}ml)"
        
        val fillLevel = if (goal > 0) {
            (total.toFloat() / goal).coerceAtMost(1.0f)
        } else {
            0f
        }
        
        // animate water level fill
        waterFillView.animate().scaleY(fillLevel).setDuration(400).start()
        
        // Update progress display
        val percentage = if (goal > 0) ((total.toFloat() / goal) * 100).toInt() else 0
        updateProgressDisplay(percentage)
    }
    
    private fun updateProgressDisplay(progress: Int) {
        progressPercentageText.text = "$progress%"
        
        // Update button text based on progress
        when {
            progress >= 100 -> {
                btnAdd.text = "🎉 Goal Reached!"
                btnAdd.isEnabled = false
            }
            progress >= 75 -> {
                btnAdd.text = "Almost There! 💧"
                btnAdd.isEnabled = true
            }
            else -> {
                btnAdd.text = "Add Glass 💧"
                btnAdd.isEnabled = true
            }
        }
    }

    //  Reminder after 1 minute
    private fun scheduleHydrationReminderInOneMinute() {
        val ctx = requireContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //  For Android 12+ (API 31+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(ctx, "Please allow exact alarms for VitaTrack.", Toast.LENGTH_LONG).show()
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.data = android.net.Uri.parse("package:${ctx.packageName}")
                startActivity(intent)
                return
            }
        }

        val intent = Intent(ctx, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            REQUEST_CODE_REMINDER,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val triggerAt = System.currentTimeMillis() + 60_000L
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)

        // Update database settings
        viewModel.updateNotificationsEnabled(true)
        Toast.makeText(ctx, "Hydration reminder set for 1 minute later!", Toast.LENGTH_SHORT).show()
    }

    //  Cancel reminder
    private fun cancelHydrationReminder() {
        val ctx = requireContext()
        val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(ctx, HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            ctx,
            REQUEST_CODE_REMINDER,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        // Update database settings
        viewModel.updateNotificationsEnabled(false)
        Toast.makeText(ctx, "Hydration reminder canceled.", Toast.LENGTH_SHORT).show()
    }
}
