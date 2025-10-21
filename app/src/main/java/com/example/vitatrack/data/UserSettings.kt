package com.example.vitatrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 1, // Single row for user settings
    val username: String = "Guest",
    val email: String = "",
    val profileImageUri: String = "", // URI of profile picture
    val bio: String = "", // User bio/description
    val dailyWaterGoalMl: Int = 2000,
    val reminderIntervalMinutes: Int = 120, // 2 hours default
    val notificationsEnabled: Boolean = true,
    val reminderStartTime: String = "08:00",
    val reminderEndTime: String = "22:00",
    val theme: String = "light", // "light", "dark", "auto"
    val firstLaunch: Boolean = true,
    val lastSyncDate: String = ""
)
