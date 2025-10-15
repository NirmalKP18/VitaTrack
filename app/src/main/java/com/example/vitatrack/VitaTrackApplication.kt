package com.example.vitatrack

import android.app.Application
import com.example.vitatrack.data.VitaTrackDatabase
import com.example.vitatrack.repository.VitaTrackRepository

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
    }
    
    companion object {
        lateinit var instance: VitaTrackApplication
            private set
    }
}
