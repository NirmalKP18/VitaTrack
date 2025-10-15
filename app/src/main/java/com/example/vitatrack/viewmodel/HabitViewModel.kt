package com.example.vitatrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.VitaTrackApplication
import com.example.vitatrack.data.Habit
import com.example.vitatrack.repository.VitaTrackRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HabitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitaTrackRepository = (application as VitaTrackApplication).repository
    
    private val _todayHabits = MutableLiveData<List<Habit>>()
    val todayHabits: LiveData<List<Habit>> = _todayHabits
    
    private val _todayCompletedCount = MutableLiveData<Int>()
    val todayCompletedCount: LiveData<Int> = _todayCompletedCount
    
    private val _todayTotalCount = MutableLiveData<Int>()
    val todayTotalCount: LiveData<Int> = _todayTotalCount
    
    private val _progressPercentage = MutableLiveData<Int>()
    val progressPercentage: LiveData<Int> = _progressPercentage
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadTodayHabits()
        observeTodayStats()
    }
    
    fun loadTodayHabits() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getTodayHabits().collect { habits ->
                    _todayHabits.value = habits
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load habits: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun observeTodayStats() {
        viewModelScope.launch {
            try {
                repository.getTodayCompletedCount().collect { completed ->
                    _todayCompletedCount.value = completed
                    updateProgressPercentage()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load completion count: ${e.message}"
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getTodayTotalCount().collect { total ->
                    _todayTotalCount.value = total
                    updateProgressPercentage()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load total count: ${e.message}"
            }
        }
    }
    
    private fun updateProgressPercentage() {
        val completed = _todayCompletedCount.value ?: 0
        val total = _todayTotalCount.value ?: 0
        val percentage = if (total > 0) (completed * 100) / total else 0
        _progressPercentage.value = percentage
    }
    
    fun addHabit(name: String, targetTime: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentDate = repository.getCurrentDate()
                val newHabit = Habit(
                    name = name,
                    targetTime = targetTime,
                    createdDate = currentDate,
                    isCompleted = false,
                    completedDate = null,
                    streak = 0,
                    totalCompletions = 0
                )
                repository.insertHabit(newHabit)
                loadTodayHabits() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add habit: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val isCompleted = !habit.isCompleted
                val completedDate = if (isCompleted) repository.getCurrentDate() else null
                val newStreak = if (isCompleted) habit.streak + 1 else maxOf(0, habit.streak - 1)
                val newTotalCompletions = if (isCompleted) habit.totalCompletions + 1 else habit.totalCompletions
                
                repository.updateHabitCompletion(
                    habit.id,
                    isCompleted,
                    completedDate,
                    newStreak,
                    newTotalCompletions
                )
                loadTodayHabits() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update habit: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteHabit(habit)
                loadTodayHabits() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete habit: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}