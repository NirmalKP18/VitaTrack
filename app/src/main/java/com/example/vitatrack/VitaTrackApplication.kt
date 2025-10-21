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
        
        // Note: Removed automatic database clearing to preserve user data
        // Database is now only cleared when user explicitly requests it
    }
    
    companion object {
        lateinit var instance: VitaTrackApplication
            private set
    }
}
