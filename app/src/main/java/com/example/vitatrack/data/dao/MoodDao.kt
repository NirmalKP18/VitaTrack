package com.example.vitatrack.data.dao

import androidx.room.*
import com.example.vitatrack.data.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    
    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>
    
    @Query("SELECT * FROM mood_entries WHERE date = :date ORDER BY time DESC")
    fun getMoodEntriesByDate(date: String): Flow<List<MoodEntry>>
    
    @Query("SELECT * FROM mood_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getMoodEntriesByDateRange(startDate: String, endDate: String): Flow<List<MoodEntry>>
    
    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getMoodEntryById(id: Long): MoodEntry?
    
    @Query("SELECT AVG(moodScore) FROM mood_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageMoodScore(startDate: String, endDate: String): Flow<Double?>
    
    @Query("SELECT emoji, COUNT(*) as count FROM mood_entries WHERE date BETWEEN :startDate AND :endDate GROUP BY emoji ORDER BY count DESC")
    fun getMoodFrequency(startDate: String, endDate: String): Flow<List<MoodFrequency>>
    
    @Query("SELECT date, AVG(moodScore) as avgScore FROM mood_entries WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date ASC")
    fun getMoodTrend(startDate: String, endDate: String): Flow<List<MoodTrend>>
    
    @Query("SELECT * FROM mood_entries WHERE date = :todayDate ORDER BY time DESC LIMIT 1")
    fun getTodayLatestMood(todayDate: String): Flow<MoodEntry?>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(moodEntry: MoodEntry): Long
    
    @Update
    suspend fun updateMoodEntry(moodEntry: MoodEntry)
    
    @Delete
    suspend fun deleteMoodEntry(moodEntry: MoodEntry)
    
    @Query("DELETE FROM mood_entries WHERE id = :id")
    suspend fun deleteMoodEntryById(id: Long)
    
    @Query("DELETE FROM mood_entries")
    suspend fun deleteAllMoodEntries()
}

data class MoodFrequency(
    val emoji: String,
    val count: Int
)

data class MoodTrend(
    val date: String,
    val avgScore: Double
)
