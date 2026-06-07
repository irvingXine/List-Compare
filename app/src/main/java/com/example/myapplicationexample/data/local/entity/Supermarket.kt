package com.example.myapplicationexample.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supermarkets")
data class Supermarket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val direccion: String? = null
)
