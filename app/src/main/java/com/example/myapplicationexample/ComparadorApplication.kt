package com.example.myapplicationexample

import android.app.Application
import androidx.room.Room
import com.example.myapplicationexample.data.local.AppDatabase
import com.example.myapplicationexample.data.repository.PreferenceManager

class ComparadorApplication : Application() {

    lateinit var preferenceManager: PreferenceManager
        private set

    // Instancia única de tu base de datos
    private var _database: AppDatabase? = null
    val database: AppDatabase
        get() {
            if (_database == null) {
                initializeDatabase()
            }
            return _database!!
        }

    override fun onCreate() {
        super.onCreate()
        preferenceManager = PreferenceManager(this)
        initializeDatabase()
    }

    private fun initializeDatabase() {
        databaseBuilder()
    }

    private fun databaseBuilder() {
        _database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "supermercados_db"
        ).fallbackToDestructiveMigration().build()
    }

    fun closeDatabase() {
        _database?.close()
        _database = null
    }
}
