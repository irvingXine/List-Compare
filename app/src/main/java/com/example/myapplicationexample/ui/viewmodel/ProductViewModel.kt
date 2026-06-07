package com.example.myapplicationexample.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationexample.data.local.dao.ProductDao
import com.example.myapplicationexample.data.local.entity.Product
import com.example.myapplicationexample.data.local.entity.UnitType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val productDao: ProductDao) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val products: StateFlow<List<Product>> = combine(
        productDao.getAllProducts(),
        _searchQuery
    ) { products, query ->
        if (query.isBlank()) products
        else products.filter { it.nombre.contains(query, ignoreCase = true) || it.codigo_barras?.contains(query) == true }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun addProduct(
        name: String,
        barcode: String?,
        unitType: UnitType,
        piecesPerPackage: Int? = null
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            productDao.insertProduct(
                Product(
                    nombre = name,
                    codigo_barras = barcode,
                    tipo_unidad = unitType,
                    piezas_por_empaque = piecesPerPackage
                )
            )
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            productDao.updateProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productDao.deleteProduct(product)
        }
    }
}
