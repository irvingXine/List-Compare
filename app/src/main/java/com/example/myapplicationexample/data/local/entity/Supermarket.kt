package com.example.myapplicationexample.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "supermarkets")
@Serializable
data class Supermarket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val direccion: String? = null
)
