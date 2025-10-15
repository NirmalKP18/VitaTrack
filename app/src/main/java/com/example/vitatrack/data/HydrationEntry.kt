package com.example.vitatrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "hydration_entries")
data class HydrationEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amountMl: Int, // amount of water in milliliters
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
    val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val time: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    val goalMl: Int = 2000, // daily goal in ml
    val entryType: String = "manual" // "manual", "reminder", "quick_add"
)
