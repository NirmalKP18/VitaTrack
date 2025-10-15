package com.example.vitatrack.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val targetTime: String, // example: "07:30 AM"
    val isCompleted: Boolean = false,
    val createdDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
    val completedDate: String? = null,
    val category: String = "General", // e.g., "Health", "Productivity", "Wellness"
    val priority: Int = 1, // 1=Low, 2=Medium, 3=High
    val streak: Int = 0, // consecutive completion days
    val totalCompletions: Int = 0
)
