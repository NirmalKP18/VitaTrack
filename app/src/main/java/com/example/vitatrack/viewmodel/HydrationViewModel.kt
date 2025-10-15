package com.example.vitatrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.VitaTrackApplication
import com.example.vitatrack.data.HydrationEntry
import com.example.vitatrack.data.UserSettings
import com.example.vitatrack.data.dao.HydrationTrend
import com.example.vitatrack.repository.VitaTrackRepository
import kotlinx.coroutines.launch

class HydrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitaTrackRepository = (application as VitaTrackApplication).repository
    
    private val _todayTotalHydration = MutableLiveData<Int>()
    val todayTotalHydration: LiveData<Int> = _todayTotalHydration
    
    private val _userSettings = MutableLiveData<UserSettings?>()
    val userSettings: LiveData<UserSettings?> = _userSettings
    
    private val _hydrationTrend = MutableLiveData<List<HydrationTrend>>()
    val hydrationTrend: LiveData<List<HydrationTrend>> = _hydrationTrend
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    init {
        loadUserSettings()
        observeTodayHydration()
        observeHydrationTrend()
    }
    
    private fun loadUserSettings() {
        viewModelScope.launch {
            try {
                repository.getUserSettings().collect { settings ->
                    _userSettings.value = settings
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load user settings: ${e.message}"
            }
        }
    }
    
    private fun observeTodayHydration() {
        viewModelScope.launch {
            try {
                repository.getTodayTotalHydration().collect { total ->
                    _todayTotalHydration.value = total ?: 0
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load today's hydration: ${e.message}"
            }
        }
    }
    
    private fun observeHydrationTrend() {
        val (startDate, endDate) = repository.getLast7DaysRange()
        
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
    
    fun addGlass() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentDate = repository.getCurrentDate()
                val currentTime = repository.getCurrentTime()
                
                val newEntry = HydrationEntry(
                    amountMl = 250, // Standard glass size
                    date = currentDate,
                    time = currentTime
                )
                repository.insertHydrationEntry(newEntry)
                observeTodayHydration() // Refresh the data
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add hydration: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                repository.updateNotificationsEnabled(enabled)
                loadUserSettings() // Refresh settings
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update notifications: ${e.message}"
            }
        }
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}