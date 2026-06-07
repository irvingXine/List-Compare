package com.example.myapplicationexample.domain

import com.example.myapplicationexample.data.local.entity.UnitType

object PriceCalculator {
    fun calculateUnitPrice(
        price: Double,
        unitType: UnitType,
        piecesPerPackage: Int? = null
    ): Double {
        return when (unitType) {
            UnitType.EMPAQUE -> {
                if (piecesPerPackage != null && piecesPerPackage > 0) {
                    price / piecesPerPackage
                } else {
                    price
                }
            }
            UnitType.KILOGRAMO -> price / 10 // Precio por 100g
            UnitType.LITRO -> price / 10 // Precio por 100ml
            UnitType.PIEZA -> price
        }
    }
}
