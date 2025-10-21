package com.example.vitatrack

import android.app.Application
import com.example.vitatrack.data.VitaTrackDatabase
import com.example.vitatrack.repository.VitaTrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VitaTrackApplication : Application() {
    
    val database by lazy { VitaTrackDatabase.getDatabase(this) }
    val repository by lazy { 
        VitaTrackRepository(
            database.habitDao(),
            database.moodDao(),
            database.hydrationDao(),
            database.userSettingsDao()
        )
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Clear all demo data from database
        CoroutineScope(Dispatchers.IO).launch {
            repository.clearAllData()
        }
    }
    
    companion object {
        lateinit var instance: VitaTrackApplication
            private set
    }
}
