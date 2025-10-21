package com.example.vitatrack.data.dao

import androidx.room.*
import com.example.vitatrack.data.HydrationEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {
    
    @Query("SELECT * FROM hydration_entries ORDER BY timestamp DESC")
    fun getAllHydrationEntries(): Flow<List<HydrationEntry>>
    
    @Query("SELECT * FROM hydration_entries WHERE date = :date ORDER BY time DESC")
    fun getHydrationEntriesByDate(date: String): Flow<List<HydrationEntry>>
    
    @Query("SELECT * FROM hydration_entries WHERE date BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getHydrationEntriesByDateRange(startDate: String, endDate: String): Flow<List<HydrationEntry>>
    
    @Query("SELECT * FROM hydration_entries WHERE id = :id")
    suspend fun getHydrationEntryById(id: Long): HydrationEntry?
    
    @Query("SELECT SUM(amountMl) FROM hydration_entries WHERE date = :date")
    fun getTotalHydrationByDate(date: String): Flow<Int?>
    
    @Query("SELECT SUM(amountMl) FROM hydration_entries WHERE date = :todayDate")
    fun getTodayTotalHydration(todayDate: String): Flow<Int?>
    
    @Query("SELECT AVG(amountMl) FROM hydration_entries WHERE date BETWEEN :startDate AND :endDate")
    fun getAverageHydration(startDate: String, endDate: String): Flow<Double?>
    
    @Query("SELECT date, SUM(amountMl) as totalMl FROM hydration_entries WHERE date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date ASC")
    fun getHydrationTrend(startDate: String, endDate: String): Flow<List<HydrationTrend>>
    
    @Query("SELECT COUNT(*) FROM hydration_entries WHERE date = :date")
    fun getHydrationEntryCountByDate(date: String): Flow<Int>
    
    @Query("SELECT * FROM hydration_entries WHERE date = :todayDate ORDER BY time DESC LIMIT 5")
    fun getTodayRecentEntries(todayDate: String): Flow<List<HydrationEntry>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHydrationEntry(hydrationEntry: HydrationEntry): Long
    
    @Update
    suspend fun updateHydrationEntry(hydrationEntry: HydrationEntry)
    
    @Delete
    suspend fun deleteHydrationEntry(hydrationEntry: HydrationEntry)
    
    @Query("DELETE FROM hydration_entries WHERE id = :id")
    suspend fun deleteHydrationEntryById(id: Long)
    
    @Query("DELETE FROM hydration_entries WHERE date = :date")
    suspend fun deleteHydrationEntriesByDate(date: String)
    
    @Query("DELETE FROM hydration_entries")
    suspend fun deleteAllHydrationEntries()
}

data class HydrationTrend(
    val date: String,
    val totalMl: Int
)
