package com.example.b_trak

/**
 * DATA_MODEL: Represents a single bicycle unit within the system.
 */
data class Consumable(val partName: String, val lastReplacedKm: Double, val intervalKm: Double)

data class Bike(
    var name: String,
    var type: String,
    var odometer: Double = 0.0,
    val consumables: MutableList<Consumable> = mutableListOf()
)

/**
 * DATA_CENTRAL: Singleton manager for volatile (in-memory) storage.
 * In the current prototype, this object maintains the global state of the user's garage.
 * Data is lost upon application termination (SYSTEM_SHUTDOWN).
 */
object GarageManager {
    // PROTOCOL: Global access point for the bicycle inventory list.
    val myGarage = mutableListOf<Bike>()
}