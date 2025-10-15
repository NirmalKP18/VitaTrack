package com.example.vitatrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.VitaTrackApplication
import com.example.vitatrack.data.Habit
import com.example.vitatrack.data.dao.MoodFrequency
import com.example.vitatrack.data.dao.MoodTrend
import com.example.vitatrack.data.dao.HydrationTrend
import com.example.vitatrack.repository.VitaTrackRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitaTrackRepository = (application as VitaTrackApplication).repository
    
    private val _todayHabits = MutableLiveData<List<Habit>>()
    val todayHabits: LiveData<List<Habit>> = _todayHabits
    
    private val _todayCompletedCount = MutableLiveData<Int>()
    val todayCompletedCount: LiveData<Int> = _todayCompletedCount
    
    private val _todayTotalCount = MutableLiveData<Int>()
    val todayTotalCount: LiveData<Int> = _todayTotalCount
    
    private val _progressPercentage = MutableLiveData<Int>()
    val progressPercentage: LiveData<Int> = _progressPercentage
    
    private val _moodFrequency = MutableLiveData<List<MoodFrequency>>()
    val moodFrequency: LiveData<List<MoodFrequency>> = _moodFrequency
    
    private val _moodTrend = MutableLiveData<List<MoodTrend>>()
    val moodTrend: LiveData<List<MoodTrend>> = _moodTrend
    
    private val _hydrationTrend = MutableLiveData<List<HydrationTrend>>()
    val hydrationTrend: LiveData<List<HydrationTrend>> = _hydrationTrend
    
    private val _todayHydration = MutableLiveData<Int>()
    val todayHydration: LiveData<Int> = _todayHydration
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadTodayData()
        observeMoodData()
        observeHydrationData()
    }
    
    private fun loadTodayData() {
        viewModelScope.launch {
            try {
                repository.getTodayHabits().collect { habits ->
                    _todayHabits.value = habits
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load today's habits: ${e.message}"
            }
        }
        
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
    
    private fun observeMoodData() {
        val (startDate, endDate) = repository.getLast7DaysRange()
        
        viewModelScope.launch {
            try {
                repository.getMoodFrequency(startDate, endDate).collect { frequency ->
                    _moodFrequency.value = frequency
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load mood frequency: ${e.message}"
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getMoodTrend(startDate, endDate).collect { trend ->
                    _moodTrend.value = trend
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load mood trend: ${e.message}"
            }
        }
    }
    
    private fun observeHydrationData() {
        val (startDate, endDate) = repository.getLast7DaysRange()
        
        viewModelScope.launch {
            try {
                repository.getTodayTotalHydration().collect { hydration ->
                    _todayHydration.value = hydration ?: 0
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load today's hydration: ${e.message}"
            }
        }
        
        viewModelScope.launch {
            try {
                repository.getHydrationTrend(startDate, endDate).collect { trend ->
                    _hydrationTrend.value = trend
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load hydration trend: ${e.message}"
            }
        }
    }
    
    private fun updateProgressPercentage() {
        val completed = _todayCompletedCount.value ?: 0
        val total = _todayTotalCount.value ?: 0
        val percentage = if (total > 0) (completed * 100) / total else 0
        _progressPercentage.value = percentage
    }
    
    fun refreshData() {
        loadTodayData()
        observeMoodData()
        observeHydrationData()
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}