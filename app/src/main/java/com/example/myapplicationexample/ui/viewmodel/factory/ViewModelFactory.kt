package com.example.myapplicationexample.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplicationexample.data.local.dao.PriceRecordDao
import com.example.myapplicationexample.data.local.dao.ProductDao
import com.example.myapplicationexample.data.local.dao.SupermarketDao
import com.example.myapplicationexample.ui.viewmodel.PriceViewModel
import com.example.myapplicationexample.ui.viewmodel.ProductViewModel
import com.example.myapplicationexample.ui.viewmodel.SupermarketViewModel

class SupermarketViewModelFactory(private val dao: SupermarketDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupermarketViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SupermarketViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProductViewModelFactory(private val dao: ProductDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class PriceViewModelFactory(
    private val priceDao: PriceRecordDao,
    private val productDao: ProductDao,
    private val supermarketDao: SupermarketDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PriceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PriceViewModel(priceDao, productDao, supermarketDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
