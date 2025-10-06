package com.example.vitatrack

data class Habit(
    val name: String,
    var isCompleted: Boolean = false,
    val targetTime: String // example: "07:30 AM"
)
