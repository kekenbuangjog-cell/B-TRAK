package com.example.b_trak

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bikes",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["userId"])]
)
data class BikeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Int,
    val name: String,
    val type: String,
    val odometer: Double,
    val iconResId: Int,
    val isSecondHand: Boolean
)

@Entity(
    tableName = "consumables",
    foreignKeys = [
        ForeignKey(
            entity = BikeEntity::class,
            parentColumns = ["id"],
            childColumns = ["bikeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["bikeId"])]
)
data class ConsumableEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bikeId: Long,
    val partName: String,
    val lastReplacedKm: Double,
    val intervalKm: Double,
    val isPreventive: Boolean,
    val diyReason: String?,
    val isUnderAudit: Boolean,
    val standardInterval: Double
)
