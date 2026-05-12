package com.example.b_trak

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ConsumableDao {
    @Insert
    suspend fun insertConsumables(consumables: List<ConsumableEntity>)

    @Query("SELECT * FROM consumables WHERE bikeId = :bikeId")
    suspend fun getConsumablesForBike(bikeId: Long): List<ConsumableEntity>

    @Update
    suspend fun updateConsumable(consumable: ConsumableEntity)

    @Query("DELETE FROM consumables WHERE bikeId = :bikeId")
    suspend fun deleteConsumablesForBike(bikeId: Long)
}
