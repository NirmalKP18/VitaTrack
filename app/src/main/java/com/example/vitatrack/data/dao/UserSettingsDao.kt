package com.example.vitatrack.data.dao

import androidx.room.*
import com.example.vitatrack.data.UserSettings
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettings?>
    
    @Query("SELECT * FROM user_settings WHERE id = 1")
    suspend fun getUserSettingsSync(): UserSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserSettings(userSettings: UserSettings)
    
    @Update
    suspend fun updateUserSettings(userSettings: UserSettings)
    
    @Query("UPDATE user_settings SET username = :username WHERE id = 1")
    suspend fun updateUsername(username: String)
    
    @Query("UPDATE user_settings SET dailyWaterGoalMl = :goal WHERE id = 1")
    suspend fun updateDailyWaterGoal(goal: Int)
    
    @Query("UPDATE user_settings SET reminderIntervalMinutes = :interval WHERE id = 1")
    suspend fun updateReminderInterval(interval: Int)
    
    @Query("UPDATE user_settings SET notificationsEnabled = :enabled WHERE id = 1")
    suspend fun updateNotificationsEnabled(enabled: Boolean)
    
    @Query("UPDATE user_settings SET reminderStartTime = :startTime WHERE id = 1")
    suspend fun updateReminderStartTime(startTime: String)
    
    @Query("UPDATE user_settings SET reminderEndTime = :endTime WHERE id = 1")
    suspend fun updateReminderEndTime(endTime: String)
    
    @Query("UPDATE user_settings SET theme = :theme WHERE id = 1")
    suspend fun updateTheme(theme: String)
    
    @Query("UPDATE user_settings SET firstLaunch = :firstLaunch WHERE id = 1")
    suspend fun updateFirstLaunch(firstLaunch: Boolean)
}
