package com.example.vitatrack.utils

import android.content.Context
import com.example.vitatrack.VitaTrackApplication
import com.example.vitatrack.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DemoDataSeeder(private val context: Context) {
    
    private val repository = (context.applicationContext as VitaTrackApplication).repository
    
    fun seedDemoData() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Clear existing data first
                clearAllData()
                
                // Seed habits
                seedHabits()
                
                // Seed mood entries
                seedMoodEntries()
                
                // Seed hydration entries
                seedHydrationEntries()
                
                // Seed user settings
                seedUserSettings()
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private suspend fun clearAllData() {
        // Note: In a real app, you'd have delete methods in your DAOs
        // For demo purposes, we'll just add new data
    }
    
    private suspend fun seedHabits() {
        val habits = listOf(
            Habit(name = "Morning Exercise", targetTime = "07:00 AM", isCompleted = true, streak = 5),
            Habit(name = "Read for 30 minutes", targetTime = "08:30 AM", isCompleted = true, streak = 3),
            Habit(name = "Meditation", targetTime = "09:00 AM", isCompleted = false, streak = 0),
            Habit(name = "Drink 8 glasses of water", targetTime = "Throughout day", isCompleted = true, streak = 7),
            Habit(name = "Evening walk", targetTime = "06:00 PM", isCompleted = false, streak = 0)
        )
        
        habits.forEach { habit ->
            repository.insertHabit(habit)
        }
    }
    
    private suspend fun seedMoodEntries() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        
        val moodEntries = listOf(
            MoodEntry(emoji = "😊", note = "Feeling great after morning workout!", moodScore = 5, 
                date = dateFormat.format(calendar.time), time = timeFormat.format(calendar.time)),
            MoodEntry(emoji = "😐", note = "Average day, nothing special", moodScore = 3,
                date = dateFormat.format(calendar.time), time = timeFormat.format(calendar.time)),
            MoodEntry(emoji = "😊", note = "Had a productive day at work", moodScore = 4,
                date = dateFormat.format(calendar.time), time = timeFormat.format(calendar.time)),
            MoodEntry(emoji = "😢", note = "Feeling a bit tired today", moodScore = 2,
                date = dateFormat.format(calendar.time), time = timeFormat.format(calendar.time)),
            MoodEntry(emoji = "😊", note = "Great mood after meeting friends", moodScore = 5,
                date = dateFormat.format(calendar.time), time = timeFormat.format(calendar.time))
        )
        
        moodEntries.forEach { mood ->
            repository.insertMoodEntry(mood)
        }
    }
    
    private suspend fun seedHydrationEntries() {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        val hydrationEntries = listOf(
            HydrationEntry(amountMl = 250, date = todayDate), // 1 glass
            HydrationEntry(amountMl = 250, date = todayDate), // 2 glasses
            HydrationEntry(amountMl = 250, date = todayDate), // 3 glasses
            HydrationEntry(amountMl = 250, date = todayDate), // 4 glasses
            HydrationEntry(amountMl = 250, date = todayDate), // 5 glasses
            HydrationEntry(amountMl = 250, date = todayDate), // 6 glasses
            HydrationEntry(amountMl = 250, date = todayDate), // 7 glasses
            HydrationEntry(amountMl = 250, date = todayDate)  // 8 glasses
        )
        
        hydrationEntries.forEach { hydration ->
            repository.insertHydrationEntry(hydration)
        }
    }
    
    private suspend fun seedUserSettings() {
        val userSettings = UserSettings(
            username = "Demo User",
            dailyWaterGoalMl = 2000,
            reminderIntervalMinutes = 120,
            notificationsEnabled = true
        )
        
        repository.insertUserSettings(userSettings)
    }
}
