package com.example.vitatrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.VitaTrackApplication
import com.example.vitatrack.data.MoodEntry
import com.example.vitatrack.data.dao.MoodFrequency
import com.example.vitatrack.data.dao.MoodTrend
import com.example.vitatrack.repository.VitaTrackRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitaTrackRepository = (application as VitaTrackApplication).repository
    
    private val _moodEntries = MutableLiveData<List<MoodEntry>>()
    val moodEntries: LiveData<List<MoodEntry>> = _moodEntries
    
    private val _moodFrequency = MutableLiveData<List<MoodFrequency>>()
    val moodFrequency: LiveData<List<MoodFrequency>> = _moodFrequency
    
    private val _moodTrend = MutableLiveData<List<MoodTrend>>()
    val moodTrend: LiveData<List<MoodTrend>> = _moodTrend
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadMoodEntries()
        observeMoodStatistics()
    }
    
    fun loadMoodEntries() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.getAllMoodEntries().collect { entries ->
                    _moodEntries.value = entries
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load mood entries: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun observeMoodStatistics() {
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
    
    fun addMoodEntry(emoji: String, note: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentDateTime = repository.getCurrentDateTime()
                val currentDate = repository.getCurrentDate()
                val currentTime = repository.getCurrentTime()
                
                // Simple mapping for mood score (1-5)
                val moodScore = when (emoji) {
                    "😊" -> 5 // Happy
                    "😐" -> 3 // Neutral
                    "😢" -> 2 // Sad
                    "😡" -> 1 // Angry
                    "😴" -> 2 // Sleepy
                    else -> 3 // Default to neutral
                }
                
                val newEntry = MoodEntry(
                    emoji = emoji,
                    note = note,
                    timestamp = currentDateTime,
                    date = currentDate,
                    time = currentTime,
                    moodScore = moodScore
                )
                repository.insertMoodEntry(newEntry)
                loadMoodEntries() // Refresh the list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save mood: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}