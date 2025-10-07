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
import com.google.android.material.button.MaterialButton

class HydrationFragment : Fragment() {

    private lateinit var waterFillView: View
    private lateinit var textProgress: TextView
    private lateinit var btnAdd: MaterialButton
    private lateinit var btnReset: MaterialButton
    private lateinit var btnSetReminder: MaterialButton
    private lateinit var btnCancelReminder: MaterialButton

    private var currentCount = 0
    private val goal = 8

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

        btnAdd.setOnClickListener { addGlass() }
        btnReset.setOnClickListener { resetProgress() }
        btnSetReminder.setOnClickListener { scheduleHydrationReminderInOneMinute() }
        btnCancelReminder.setOnClickListener { cancelHydrationReminder() }

        updateUI()
        return view
    }

    //  Add glass logic with animation
    private fun addGlass() {
        if (currentCount < goal) {
            currentCount++
            updateUI()
        } else {
            Toast.makeText(requireContext(), "Goal reached! 🎉", Toast.LENGTH_SHORT).show()
        }
    }

    //  Reset progress
    private fun resetProgress() {
        currentCount = 0
        updateUI()
    }

    //  Update progress + animation
    private fun updateUI() {
        textProgress.text = "$currentCount / $goal glasses"
        val fillLevel = currentCount.toFloat() / goal
        // animate water level fill
        waterFillView.animate().scaleY(fillLevel).setDuration(400).start()
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

        Toast.makeText(ctx, "Hydration reminder canceled.", Toast.LENGTH_SHORT).show()
    }
}
