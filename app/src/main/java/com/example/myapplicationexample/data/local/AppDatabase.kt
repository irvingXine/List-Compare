package com.example.myapplicationexample.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myapplicationexample.data.local.dao.PriceRecordDao
import com.example.myapplicationexample.data.local.dao.ProductDao
import com.example.myapplicationexample.data.local.dao.SupermarketDao
import com.example.myapplicationexample.data.local.entity.PriceRecord
import com.example.myapplicationexample.data.local.entity.Product
import com.example.myapplicationexample.data.local.entity.Supermarket

@Database(
    entities = [Supermarket::class, Product::class, PriceRecord::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun supermarketDao(): SupermarketDao
    abstract fun productDao(): ProductDao
    abstract fun priceRecordDao(): PriceRecordDao
}
