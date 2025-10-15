package com.example.vitatrack.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.vitatrack.data.dao.HabitDao
import com.example.vitatrack.data.dao.MoodDao
import com.example.vitatrack.data.dao.HydrationDao
import com.example.vitatrack.data.dao.UserSettingsDao

@Database(
    entities = [
        Habit::class,
        MoodEntry::class,
        HydrationEntry::class,
        UserSettings::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class VitaTrackDatabase : RoomDatabase() {
    
    abstract fun habitDao(): HabitDao
    abstract fun moodDao(): MoodDao
    abstract fun hydrationDao(): HydrationDao
    abstract fun userSettingsDao(): UserSettingsDao
    
    companion object {
        @Volatile
        private var INSTANCE: VitaTrackDatabase? = null
        
        fun getDatabase(context: Context): VitaTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VitaTrackDatabase::class.java,
                    "vitatrack_database"
                )
                .fallbackToDestructiveMigration() // For development - remove in production
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

