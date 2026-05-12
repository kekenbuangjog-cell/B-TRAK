package com.example.b_trak

/**
 * Represents a single maintenance item within the B-Trak system.
 * This class stores data regarding the lifecycle of a specific bicycle part or a recurring maintenance task.
 * 
 * @property partName The display name of the component or task (e.g., "Drive Chain", "Deep Clean").
 * @property lastReplacedKm The odometer reading (in KM) at the time this part was last replaced or serviced.
 * @property intervalKm The standard distance (in KM) after which this part should be replaced or serviced.
 * @property isPreventive A flag indicating if this is a recurring DIY task rather than a hardware replacement.
 * @property diyReason An optional educational description explaining why a preventive task is important.
 * @property isUnderAudit A flag indicating if this part requires an early safety inspection (used for second-hand bikes).
 * @property standardInterval Stores the original lifespan of the part, used to restore the [intervalKm] after an audit clears.
 */
data class Consumable(
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
 * Contains the hardware details, cumulative mileage, and a list of specific parts attached to it.
 * 
 * @property name The user-defined nickname for the bicycle.
 * @property type The discipline category of the bicycle (e.g., "Road Bike", "MTB XC").
 * @property odometer The total cumulative distance ridden on this bicycle.
 * @property iconResId The drawable resource ID used to display the bike's visual icon in the UI.
 * @property consumables A mutable collection holding all active maintenance parts and tasks for this specific bike.
 * @property isSecondHand A flag indicating if the bicycle was purchased used, triggering safety audits for unknown part wear.
 */
data class Bike(
    var name: String,
    var type: String,
    var odometer: Double = 0.0,
    var iconResId: Int = R.drawable.logo_placeholder,
    val consumables: MutableList<Consumable> = mutableListOf(),
    var isSecondHand: Boolean = false
)

/**
 * Singleton object that manages the central repository of registered bicycles.
 * It serves as a volatile, in-memory database for the application's current lifecycle.
 * Note: Data stored here will be cleared when the app is completely shut down.
 */
object GarageManager {
    /**
     * The global mutable list acting as the central garage inventory. 
     * All views and adapters read from and mutate this specific collection.
     */
    val myGarage = mutableListOf<Bike>()
}