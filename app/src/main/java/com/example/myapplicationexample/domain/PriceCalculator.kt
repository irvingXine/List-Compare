package com.example.myapplicationexample.domain

import com.example.myapplicationexample.data.local.entity.UnitType

object PriceCalculator {
    fun calculateUnitPrice(
        price: Double,
        unitType: UnitType,
        piecesPerPackage: Int? = null,
        cantidadUnidad: Double = 1.0 // Nueva cantidad variable
    ): Double {
        // Primero normalizamos el precio a la unidad base (1kg, 1L, 1 pieza)
        val normalizedPrice = if (cantidadUnidad > 0) price / cantidadUnidad else price

        return when (unitType) {
            UnitType.EMPAQUE -> {
                if (piecesPerPackage != null && piecesPerPackage > 0) {
                    normalizedPrice / piecesPerPackage
                } else {
                    normalizedPrice
                }
            }
            UnitType.KILOGRAMO -> normalizedPrice / 10 // Precio por 100g
            UnitType.LITRO -> normalizedPrice / 10 // Precio por 100ml
            UnitType.PIEZA -> normalizedPrice
        }
    }
}
