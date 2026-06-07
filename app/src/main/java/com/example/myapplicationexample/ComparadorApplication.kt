package com.example.myapplicationexample

import android.app.Application
import androidx.room.Room
import com.example.myapplicationexample.data.local.AppDatabase

class ComparadorApplication : Application() {

    // Instancia única de tu base de datos
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()

        // Aquí se construye la base de datos física en el teléfono
        database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "supermercados_db"
        ).fallbackToDestructiveMigration().build()
    }
}
