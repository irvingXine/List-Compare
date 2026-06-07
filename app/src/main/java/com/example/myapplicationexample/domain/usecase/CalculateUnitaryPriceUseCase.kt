package com.example.myapplicationexample.domain.usecase

import com.example.myapplicationexample.data.local.entity.Product
import com.example.myapplicationexample.data.local.entity.UnitType

class CalculateUnitaryPriceUseCase {
    /**
     * Calcula el precio desglosado basándose en el tipo de unidad del producto.
     * Retorna el precio calculado y una descripción de la unidad calculada.
     */
    operator fun invoke(precioIngresado: Double, product: Product): Pair<Double, String> {
        return when (product.tipo_unidad) {
            UnitType.EMPAQUE -> {
                val piezas = product.piezas_por_empaque ?: 1
                val precioPorPieza = if (piezas > 0) precioIngresado / piezas else precioIngresado
                Pair(precioPorPieza, "por pieza")
            }
            UnitType.KILOGRAMO -> {
                val precioPor100g = precioIngresado / 10.0
                Pair(precioPor100g, "por 100g")
            }
            UnitType.LITRO -> {
                val precioPor100ml = precioIngresado / 10.0
                Pair(precioPor100ml, "por 100ml")
            }
            UnitType.PIEZA -> {
                Pair(precioIngresado, "por pieza")
            }
        }
    }
}
