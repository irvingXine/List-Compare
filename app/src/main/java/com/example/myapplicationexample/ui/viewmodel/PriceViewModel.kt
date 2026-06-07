package com.example.myapplicationexample.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationexample.data.local.dao.PriceRecordDao
import com.example.myapplicationexample.data.local.dao.ProductDao
import com.example.myapplicationexample.data.local.dao.SupermarketDao
import com.example.myapplicationexample.data.local.entity.PriceRecord
import com.example.myapplicationexample.data.local.entity.Product
import com.example.myapplicationexample.data.local.entity.Supermarket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PriceEntry(
    val record: PriceRecord,
    val product: Product,
    val supermarket: Supermarket
)

class PriceViewModel(
    private val priceDao: PriceRecordDao,
    private val productDao: ProductDao,
    private val supermarketDao: SupermarketDao
) : ViewModel() {

    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery

    private val _selectedProductId = MutableStateFlow<Long?>(null)
    val selectedProductId: StateFlow<Long?> = _selectedProductId

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val filteredProducts: StateFlow<List<Product>> = combine(
        productDao.getAllProducts(),
        _productSearchQuery
    ) { products, query ->
        if (query.isBlank()) emptyList()
        else products.filter { it.nombre.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supermarkets = supermarketDao.getAllSupermarkets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val priceEntries: StateFlow<List<PriceEntry>> = combine(
        priceDao.getAllPriceRecords(),
        productDao.getAllProducts(),
        supermarketDao.getAllSupermarkets(),
        _selectedProductId
    ) { records, products, supermarkets, selectedId ->
        val productMap = products.associateBy { it.id }
        val supermarketMap = supermarkets.associateBy { it.id }
        
        records.filter { if (selectedId != null) it.product_id == selectedId else true }
            .mapNotNull { record ->
                val product = productMap[record.product_id]
                val supermarket = supermarketMap[record.supermarket_id]
                if (product != null && supermarket != null) {
                    PriceEntry(record, product, supermarket)
                } else null
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onProductSearchQueryChange(query: String) {
        _productSearchQuery.value = query
    }

    fun selectProduct(id: Long?) {
        _selectedProductId.value = id
        _errorMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun addPrice(productId: Long, supermarketId: Long, price: Double, notes: String? = null) {
        viewModelScope.launch {
            // Check for duplicates
            val allRecords = priceDao.getAllPriceRecords().first()
            val exists = allRecords.any { it.product_id == productId && it.supermarket_id == supermarketId }
            
            if (exists) {
                _errorMessage.value = "Este producto ya tiene un precio asignado en esta tienda."
                return@launch
            }

            priceDao.insertPriceRecord(
                PriceRecord(
                    product_id = productId,
                    supermarket_id = supermarketId,
                    precio_ingresado = price,
                    fecha_registro = System.currentTimeMillis(),
                    notas = notes
                )
            )
            _errorMessage.value = null
        }
    }

    fun updatePrice(record: PriceRecord) {
        viewModelScope.launch {
            priceDao.updatePriceRecord(record)
        }
    }

    fun deletePrice(record: PriceRecord) {
        viewModelScope.launch {
            priceDao.deletePriceRecord(record)
        }
    }
}
