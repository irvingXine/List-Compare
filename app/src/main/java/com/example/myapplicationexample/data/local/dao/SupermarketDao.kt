package com.example.myapplicationexample.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplicationexample.data.local.entity.Supermarket
import kotlinx.coroutines.flow.Flow

@Dao
interface SupermarketDao {
    @Query("SELECT * FROM supermarkets")
    fun getAllSupermarkets(): Flow<List<Supermarket>>

    @Query("SELECT * FROM supermarkets WHERE id = :id")
    suspend fun getSupermarketById(id: Long): Supermarket?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupermarket(supermarket: Supermarket): Long

    @Update
    suspend fun updateSupermarket(supermarket: Supermarket)

    @Delete
    suspend fun deleteSupermarket(supermarket: Supermarket)
}
