package com.example.vitatrack.data.dao

import androidx.room.*
import com.example.vitatrack.data.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    
    @Query("SELECT * FROM habits ORDER BY createdDate DESC")
    fun getAllHabits(): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE createdDate = :todayDate ORDER BY targetTime ASC")
    fun getTodayHabits(todayDate: String): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): Habit?
    
    @Query("SELECT COUNT(*) FROM habits WHERE isCompleted = 1 AND createdDate = :todayDate")
    fun getTodayCompletedCount(todayDate: String): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM habits WHERE createdDate = :todayDate")
    fun getTodayTotalCount(todayDate: String): Flow<Int>
    
    @Query("SELECT * FROM habits WHERE date(createdDate) BETWEEN :startDate AND :endDate ORDER BY createdDate DESC")
    fun getHabitsByDateRange(startDate: String, endDate: String): Flow<List<Habit>>
    
    @Query("SELECT * FROM habits WHERE category = :category ORDER BY createdDate DESC")
    fun getHabitsByCategory(category: String): Flow<List<Habit>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long
    
    @Update
    suspend fun updateHabit(habit: Habit)
    
    @Delete
    suspend fun deleteHabit(habit: Habit)
    
    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteHabitById(id: Long)
    
    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
    
    @Query("UPDATE habits SET isCompleted = :isCompleted, completedDate = :completedDate, streak = :streak, totalCompletions = :totalCompletions WHERE id = :id")
    suspend fun updateHabitCompletion(id: Long, isCompleted: Boolean, completedDate: String?, streak: Int, totalCompletions: Int)
    
    @Query("SELECT AVG(CASE WHEN isCompleted = 1 THEN 1.0 ELSE 0.0 END) * 100 FROM habits WHERE date(createdDate) BETWEEN :startDate AND :endDate")
    fun getCompletionPercentage(startDate: String, endDate: String): Flow<Double>
}
