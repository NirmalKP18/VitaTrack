package com.example.vitatrack

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class HydrationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_hydration, container, false)

        val btnStart = view.findViewById<Button>(R.id.btnStartReminder)
        val btnStop = view.findViewById<Button>(R.id.btnStopReminder)

        btnStart.setOnClickListener { startHydrationReminder() }
        btnStop.setOnClickListener { stopHydrationReminder() }

        return view
    }

    private fun startHydrationReminder() {
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // For testing — trigger every 1 minute (60 * 1000 ms)
        val interval = 1 * 60 * 1000L
        val triggerAt = System.currentTimeMillis() + 5000 // first in 5 sec

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            interval,
            pendingIntent
        )

        Toast.makeText(requireContext(), "Hydration reminders ON 💧", Toast.LENGTH_SHORT).show()
    }

    private fun stopHydrationReminder() {
        val intent = Intent(requireContext(), HydrationReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)

        Toast.makeText(requireContext(), "Hydration reminders OFF 🚫", Toast.LENGTH_SHORT).show()
    }
}
