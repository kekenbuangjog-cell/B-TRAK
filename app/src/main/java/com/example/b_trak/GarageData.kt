package com.example.b_trak

/**
 * DATA_MODEL: Represents a single bicycle unit within the system.
 */
data class Bike(val name: String, val type: String)

/**
 * DATA_CENTRAL: Singleton manager for volatile (in-memory) storage.
 * In the current prototype, this object maintains the global state of the user's garage.
 * Data is lost upon application termination (SYSTEM_SHUTDOWN).
 */
object GarageManager {
    // PROTOCOL: Global access point for the bicycle inventory list.
    val myGarage = mutableListOf<Bike>()
}