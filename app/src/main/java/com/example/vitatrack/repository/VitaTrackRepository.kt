package com.example.vitatrack.repository

import com.example.vitatrack.data.*
import com.example.vitatrack.data.dao.*
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class VitaTrackRepository(
    private val habitDao: HabitDao,
    private val moodDao: MoodDao,
    private val hydrationDao: HydrationDao,
    private val userSettingsDao: UserSettingsDao
) {
    
    // Habit operations
    fun getAllHabits(): Flow<List<Habit>> = habitDao.getAllHabits()
    fun getTodayHabits(): Flow<List<Habit>> = habitDao.getTodayHabits(getCurrentDate())
    suspend fun getHabitById(id: Long): Habit? = habitDao.getHabitById(id)
    fun getTodayCompletedCount(): Flow<Int> = habitDao.getTodayCompletedCount(getCurrentDate())
    fun getTodayTotalCount(): Flow<Int> = habitDao.getTodayTotalCount(getCurrentDate())
    fun getHabitsByDateRange(startDate: String, endDate: String): Flow<List<Habit>> = 
        habitDao.getHabitsByDateRange(startDate, endDate)
    fun getHabitsByCategory(category: String): Flow<List<Habit>> = 
        habitDao.getHabitsByCategory(category)
    
    suspend fun insertHabit(habit: Habit): Long = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: Habit) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: Habit) = habitDao.deleteHabit(habit)
    suspend fun deleteHabitById(id: Long) = habitDao.deleteHabitById(id)
    suspend fun updateHabitCompletion(id: Long, isCompleted: Boolean, completedDate: String?, streak: Int, totalCompletions: Int) = 
        habitDao.updateHabitCompletion(id, isCompleted, completedDate, streak, totalCompletions)
    fun getCompletionPercentage(startDate: String, endDate: String): Flow<Double> = 
        habitDao.getCompletionPercentage(startDate, endDate)
    
    // Mood operations
    fun getAllMoodEntries(): Flow<List<MoodEntry>> = moodDao.getAllMoodEntries()
    fun getMoodEntriesByDate(date: String): Flow<List<MoodEntry>> = moodDao.getMoodEntriesByDate(date)
    fun getMoodEntriesByDateRange(startDate: String, endDate: String): Flow<List<MoodEntry>> = 
        moodDao.getMoodEntriesByDateRange(startDate, endDate)
    suspend fun getMoodEntryById(id: Long): MoodEntry? = moodDao.getMoodEntryById(id)
    fun getAverageMoodScore(startDate: String, endDate: String): Flow<Double?> = 
        moodDao.getAverageMoodScore(startDate, endDate)
    fun getMoodFrequency(startDate: String, endDate: String): Flow<List<MoodFrequency>> = 
        moodDao.getMoodFrequency(startDate, endDate)
    fun getMoodTrend(startDate: String, endDate: String): Flow<List<MoodTrend>> = 
        moodDao.getMoodTrend(startDate, endDate)
    fun getTodayLatestMood(): Flow<MoodEntry?> = moodDao.getTodayLatestMood(getCurrentDate())
    
    suspend fun insertMoodEntry(moodEntry: MoodEntry): Long = moodDao.insertMoodEntry(moodEntry)
    suspend fun updateMoodEntry(moodEntry: MoodEntry) = moodDao.updateMoodEntry(moodEntry)
    suspend fun deleteMoodEntry(moodEntry: MoodEntry) = moodDao.deleteMoodEntry(moodEntry)
    suspend fun deleteMoodEntryById(id: Long) = moodDao.deleteMoodEntryById(id)
    
    // Hydration operations
    fun getAllHydrationEntries(): Flow<List<HydrationEntry>> = hydrationDao.getAllHydrationEntries()
    fun getHydrationEntriesByDate(date: String): Flow<List<HydrationEntry>> = 
        hydrationDao.getHydrationEntriesByDate(date)
    fun getHydrationEntriesByDateRange(startDate: String, endDate: String): Flow<List<HydrationEntry>> = 
        hydrationDao.getHydrationEntriesByDateRange(startDate, endDate)
    suspend fun getHydrationEntryById(id: Long): HydrationEntry? = hydrationDao.getHydrationEntryById(id)
    fun getTotalHydrationByDate(date: String): Flow<Int?> = hydrationDao.getTotalHydrationByDate(date)
    fun getTodayTotalHydration(): Flow<Int?> = hydrationDao.getTodayTotalHydration(getCurrentDate())
    fun getAverageHydration(startDate: String, endDate: String): Flow<Double?> = 
        hydrationDao.getAverageHydration(startDate, endDate)
    fun getHydrationTrend(startDate: String, endDate: String): Flow<List<HydrationTrend>> = 
        hydrationDao.getHydrationTrend(startDate, endDate)
    fun getHydrationEntryCountByDate(date: String): Flow<Int> = 
        hydrationDao.getHydrationEntryCountByDate(date)
    fun getTodayRecentEntries(): Flow<List<HydrationEntry>> = hydrationDao.getTodayRecentEntries(getCurrentDate())
    
    suspend fun insertHydrationEntry(hydrationEntry: HydrationEntry): Long = 
        hydrationDao.insertHydrationEntry(hydrationEntry)
    suspend fun updateHydrationEntry(hydrationEntry: HydrationEntry) = 
        hydrationDao.updateHydrationEntry(hydrationEntry)
    suspend fun deleteHydrationEntry(hydrationEntry: HydrationEntry) = 
        hydrationDao.deleteHydrationEntry(hydrationEntry)
    suspend fun deleteHydrationEntryById(id: Long) = hydrationDao.deleteHydrationEntryById(id)
    suspend fun deleteHydrationEntriesByDate(date: String) = 
        hydrationDao.deleteHydrationEntriesByDate(date)
    
    // User Settings operations
    fun getUserSettings(): Flow<UserSettings?> = userSettingsDao.getUserSettings()
    suspend fun getUserSettingsSync(): UserSettings? = userSettingsDao.getUserSettingsSync()
    suspend fun insertUserSettings(userSettings: UserSettings) = userSettingsDao.insertUserSettings(userSettings)
    suspend fun updateUserSettings(userSettings: UserSettings) = userSettingsDao.updateUserSettings(userSettings)
    suspend fun updateUsername(username: String) = userSettingsDao.updateUsername(username)
    suspend fun updateDailyWaterGoal(goal: Int) = userSettingsDao.updateDailyWaterGoal(goal)
    suspend fun updateReminderInterval(interval: Int) = userSettingsDao.updateReminderInterval(interval)
    suspend fun updateNotificationsEnabled(enabled: Boolean) = userSettingsDao.updateNotificationsEnabled(enabled)
    suspend fun updateReminderStartTime(startTime: String) = userSettingsDao.updateReminderStartTime(startTime)
    suspend fun updateReminderEndTime(endTime: String) = userSettingsDao.updateReminderEndTime(endTime)
    suspend fun updateTheme(theme: String) = userSettingsDao.updateTheme(theme)
    suspend fun updateFirstLaunch(firstLaunch: Boolean) = userSettingsDao.updateFirstLaunch(firstLaunch)
    
    // Utility functions
    fun getCurrentDate(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    fun getCurrentDateTime(): String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    fun getCurrentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    
    // Get date range for last 7 days
    fun getLast7DaysRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, -6)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        
        return Pair(startDate, endDate)
    }
    
    // Get date range for last 30 days
    fun getLast30DaysRange(): Pair<String, String> {
        val calendar = Calendar.getInstance()
        val endDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, -29)
        val startDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        
        return Pair(startDate, endDate)
    }
}
