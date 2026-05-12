package com.example.b_trak

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BikeDao {
    @Insert
    suspend fun insertBike(bike: BikeEntity): Long

    @Query("SELECT * FROM bikes WHERE userId = :userId")
    suspend fun getBikesForUser(userId: Int): List<BikeEntity>

    @Update
    suspend fun updateBike(bike: BikeEntity)

    @Query("UPDATE bikes SET odometer = :odometer WHERE id = :id")
    suspend fun updateBikeOdometer(id: Long, odometer: Double)

    @Query("UPDATE bikes SET name = :newName WHERE id = :id")
    suspend fun updateBikeName(id: Long, newName: String)

    @Delete
    suspend fun deleteBike(bike: BikeEntity)
}
