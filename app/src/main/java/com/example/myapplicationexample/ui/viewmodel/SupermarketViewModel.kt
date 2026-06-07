package com.example.myapplicationexample.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationexample.data.local.dao.SupermarketDao
import com.example.myapplicationexample.data.local.entity.Supermarket
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SupermarketViewModel(private val supermarketDao: SupermarketDao) : ViewModel() {

    val supermarkets: StateFlow<List<Supermarket>> = supermarketDao.getAllSupermarkets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addSupermarket(name: String, address: String? = null) {
        if (name.isBlank()) return
        viewModelScope.launch {
            supermarketDao.insertSupermarket(Supermarket(nombre = name, direccion = address))
        }
    }

    fun deleteSupermarket(supermarket: Supermarket) {
        viewModelScope.launch {
            supermarketDao.deleteSupermarket(supermarket)
        }
    }
    
    fun updateSupermarket(supermarket: Supermarket) {
        viewModelScope.launch {
            supermarketDao.updateSupermarket(supermarket)
        }
    }
}
