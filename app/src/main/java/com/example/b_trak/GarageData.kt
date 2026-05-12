package com.example.b_trak

/**
 * Represents a single maintenance item within the B-Trak system.
 */
data class Consumable(
    val id: Long = 0,
    val partName: String,
    val lastReplacedKm: Double,
    val intervalKm: Double,
    val isPreventive: Boolean = false,
    val diyReason: String? = null,
    val isUnderAudit: Boolean = false,
    val standardInterval: Double = intervalKm
)

/**
 * Represents a bicycle registered in the user's garage.
 */
data class Bike(
    val id: Long = 0,
    var name: String,
    var type: String,
    var odometer: Double = 0.0,
    var iconResId: Int = R.drawable.logo_placeholder,
    val consumables: MutableList<Consumable> = mutableListOf(),
    var isSecondHand: Boolean = false
)

/**
 * Singleton object that manages the central repository of registered bicycles.
 * Note: Now acts as a temporary cache, but the database is the source of truth.
 */
object GarageManager {
    val myGarage = mutableListOf<Bike>()
}