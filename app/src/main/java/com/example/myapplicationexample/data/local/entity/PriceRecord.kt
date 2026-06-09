package com.example.myapplicationexample.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "price_records",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Supermarket::class,
            parentColumns = ["id"],
            childColumns = ["supermarket_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("product_id"),
        Index("supermarket_id")
    ]
)
@Serializable
data class PriceRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val product_id: Long,
    val supermarket_id: Long,
    val precio_ingresado: Double,
    val cantidad_unidad: Double = 1.0, // Nueva cantidad (ej. 1.8 kg)
    val fecha_registro: Long,
    val notas: String? = null
)
