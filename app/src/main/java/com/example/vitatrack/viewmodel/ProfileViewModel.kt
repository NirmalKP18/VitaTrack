package com.example.vitatrack.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitatrack.VitaTrackApplication
import com.example.vitatrack.data.UserSettings
import com.example.vitatrack.repository.VitaTrackRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VitaTrackRepository = (application as VitaTrackApplication).repository
    
    // LiveData for user settings
    val userSettings: LiveData<UserSettings?> = repository.getUserSettings().asLiveData()
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    /**
     * Initialize user settings if not exists
     */
    fun initializeUserSettings() {
        viewModelScope.launch {
            try {
                val existing = repository.getUserSettingsSync()
                if (existing == null) {
                    // Create default user settings
                    repository.insertUserSettings(UserSettings())
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to initialize user settings: ${e.message}"
            }
        }
    }
    
    /**
     * Update username
     */
    fun updateUsername(username: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateUsername(username)
                _successMessage.value = "Username updated successfully!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update username: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update email
     */
    fun updateEmail(email: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateEmail(email)
                _successMessage.value = "Email updated successfully!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update email: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update profile image URI
     */
    fun updateProfileImage(uri: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateProfileImage(uri)
                _successMessage.value = "Profile picture updated!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile picture: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update bio
     */
    fun updateBio(bio: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.updateBio(bio)
                _successMessage.value = "Bio updated successfully!"
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update bio: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Update all profile data at once
     */
    fun updateProfile(username: String, email: String, bio: String, profileImageUri: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentSettings = repository.getUserSettingsSync()
                if (currentSettings != null) {
                    val updatedSettings = currentSettings.copy(
                        username = username,
                        email = email,
                        bio = bio,
                        profileImageUri = profileImageUri
                    )
                    repository.updateUserSettings(updatedSettings)
                    _successMessage.value = "Profile updated successfully!"
                } else {
                    // Create new if doesn't exist
                    val newSettings = UserSettings(
                        username = username,
                        email = email,
                        bio = bio,
                        profileImageUri = profileImageUri
                    )
                    repository.insertUserSettings(newSettings)
                    _successMessage.value = "Profile created successfully!"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearMessages() {
        _successMessage.value = null
        _errorMessage.value = null
    }
}

