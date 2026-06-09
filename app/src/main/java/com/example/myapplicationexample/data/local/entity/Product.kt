package com.example.myapplicationexample.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "products")
@Serializable
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val codigo_barras: String?,
    val tipo_unidad: UnitType,
    val piezas_por_empaque: Int?
)
