package com.example.myapplicationexample.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplicationexample.data.local.entity.PriceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceRecordDao {
    @Query("SELECT * FROM price_records WHERE product_id = :productId")
    fun getPricesForProduct(productId: Long): Flow<List<PriceRecord>>
    
    @Query("SELECT * FROM price_records")
    fun getAllPriceRecords(): Flow<List<PriceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPriceRecord(priceRecord: PriceRecord): Long

    @Update
    suspend fun updatePriceRecord(priceRecord: PriceRecord)

    @Delete
    suspend fun deletePriceRecord(priceRecord: PriceRecord)
}
