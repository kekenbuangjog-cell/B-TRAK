package com.example.b_trak

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

/**
 * BikeDetailActivity provides a comprehensive view of a single bicycle's maintenance status.
 * It calculates component wear based on odometer readings and manages both hardware parts 
 * (Lifecycle Monitoring) and service tasks (Preventive Maintenance).
 */
class BikeDetailActivity : AppCompatActivity() {

    // Index of the bike in the GarageManager list
    private var bikeIndex: Int = -1
    // The specific bike object being viewed
    private lateinit var bike: Bike

    /**
     * Initializes the activity, retrieves the bike data, and sets up the UI components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind to the detailed view layout
        setContentView(R.layout.activity_bike_details)

        // Extract the bike index passed via the intent from MainActivity
        bikeIndex = intent.getIntExtra("BIKE_INDEX", -1)
        // Safety check: Close activity if the index is invalid
        if (bikeIndex == -1 || bikeIndex >= GarageManager.myGarage.size) {
            finish()
            return
        }

        // Retrieve the bike object from the central registry
        bike = GarageManager.myGarage[bikeIndex]

        // Ensure the bike has its initial set of components/tasks
        ensureDefaultConsumables()
        // Connect data to UI elements
        setupUI()
    }

    /**
     * Checks if the bike has any maintenance items registered.
     * If empty, it populates the bike with default professional-grade components and tasks.
     * Includes specialized logic for second-hand bikes to trigger initial safety audits.
     */
    private fun ensureDefaultConsumables() {
        if (bike.consumables.isEmpty()) {
            val defaults = mutableListOf<Consumable>()
            
            // Lifecycle Monitoring: Critical hardware parts
            val lifecycleParts = listOf(
                Triple("Drive Chain", 3000.0, "Drive Chain"),
                Triple("Front Tire", 5000.0, "Front Tire"),
                Triple("Rear Tire", 5000.0, "Rear Tire"),
                Triple("Front Brake Pads", 2500.0, "Front Brake Pads"),
                Triple("Rear Brake Pads", 2500.0, "Rear Brake Pads")
            )

            for (part in lifecycleParts) {
                if (bike.isSecondHand) {
                    // SECOND-HAND PROTOCOL: Force a safety audit after 50KM of use
                    defaults.add(Consumable(part.first, bike.odometer, 50.0, isUnderAudit = true, standardInterval = part.second))
                } else {
                    // STANDARD PROTOCOL: Use normal professional intervals
                    defaults.add(Consumable(part.first, bike.odometer, part.second))
                }
            }
            
            // Preventive Maintenance: Recurring DIY service tasks
            defaults.addAll(listOf(
                Consumable("Drivetrain Lube", bike.odometer, 150.0, true, "Regular lubrication minimizes friction, prevents annoying squeaks, and protects against rust. It's a quick 2-minute job that significantly extends the life of your cassette and chain."),
                Consumable("Safety Bolt Check", bike.odometer, 500.0, true, "Vibrations from riding can loosen critical bolts over time. Use a torque wrench or basic Allen keys to ensure your stem, handlebars, seatpost, and axles are securely tightened."),
                Consumable("Deep Clean/Degrease", bike.odometer, 200.0, true, "A thorough wash and degrease removes abrasive grit from your moving parts. This keeps your bike running silently and looking fresh, while preventing premature wear on expensive components."),
                Consumable("Brake Alignment", bike.odometer, 1500.0, true, "Brake cables stretch and pads wear down with use. Realigning the calipers and adjusting cable tension ensures you maintain responsive and safe stopping power.")
            ))
            
            // Inject all defaults into the bike object
            bike.consumables.addAll(defaults)
        }
    }

    /**
     * Binds the bike's properties (name, type, odometer, icon) to the header UI elements.
     * Configures the button to allow users to update the current mileage.
     */
    private fun setupUI() {
        findViewById<TextView>(R.id.detail_bike_name).text = bike.name
        findViewById<TextView>(R.id.detail_bike_type).text = bike.type
        findViewById<TextView>(R.id.detail_total_km).text = bike.odometer.toString()
        findViewById<android.widget.ImageView>(R.id.detail_bike_icon).setImageResource(bike.iconResId)

        // Set listener for the odometer update button
        findViewById<Button>(R.id.btn_update_odometer).setOnClickListener {
            showUpdateOdometerDialog()
        }

        // Generate and display the maintenance lists
        refreshMaintenanceLists()
    }

    /**
     * Displays a dialog allowing the user to manually input the new odometer reading.
     * Includes validation to ensure mileage only moves forward.
     */
    private fun showUpdateOdometerDialog() {
        val input = EditText(this)
        input.hint = "Enter new odometer reading"
        input.setText(bike.odometer.toString())

        AlertDialog.Builder(this)
            .setTitle("UPDATE ODOMETER")
            .setView(input)
            .setPositiveButton("UPDATE") { _, _ ->
                val newValue = input.text.toString().toDoubleOrNull()
                if (newValue != null) {
                    // SECURITY: Odometer cannot be decreased
                    if (newValue < bike.odometer) {
                        Toast.makeText(this, "ERROR: ODOMETER CANNOT GO BACKWARDS", Toast.LENGTH_SHORT).show()
                    } else {
                        // Update data model and UI label
                        bike.odometer = newValue
                        findViewById<TextView>(R.id.detail_total_km).text = bike.odometer.toString()
                        // Re-calculate wear for all parts
                        refreshMaintenanceLists()
                    }
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    /**
     * Clears and regenerates the maintenance task lists in the UI.
     * Categorizes items into "Preventive" or "Lifecycle" containers.
     * Calculates wear percentage and determines the status (STABLE, REPLACE NOW, etc.).
     */
    private fun refreshMaintenanceLists() {
        val lifecycleContainer = findViewById<LinearLayout>(R.id.lifecycle_container)
        val preventiveContainer = findViewById<LinearLayout>(R.id.preventive_container)

        // Clear existing views to prevent duplication
        lifecycleContainer.removeAllViews()
        preventiveContainer.removeAllViews()

        val inflater = LayoutInflater.from(this)

        // Iterate through all components registered to this bike
        for ((index, consumable) in bike.consumables.withIndex()) {
            // Inflate a new item row layout
            val itemView = inflater.inflate(R.layout.item_consumable, (if (consumable.isPreventive) preventiveContainer else lifecycleContainer), false)
            
            // Set the component name
            val nameText = itemView.findViewById<TextView>(R.id.text_part_name)
            nameText.text = consumable.partName.uppercase()
            
            // LOGIC BRANCH 1: Item is currently under a second-hand safety audit
            if (consumable.isUnderAudit && (bike.odometer - consumable.lastReplacedKm >= consumable.intervalKm)) {
                itemView.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("INSPECT ${consumable.partName.uppercase()}")
                        .setMessage("This is a second-hand component. Have you checked it properly to ensure it is safe and in good condition?")
                        .setPositiveButton("YES, IT'S GOOD") { _, _ ->
                            // Restore professional interval and clear audit flag
                            val updatedConsumable = consumable.copy(
                                isUnderAudit = false,
                                intervalKm = consumable.standardInterval,
                                lastReplacedKm = bike.odometer
                            )
                            bike.consumables[index] = updatedConsumable
                            refreshMaintenanceLists()
                            Toast.makeText(this, "TASK COMPLETED: ${consumable.partName}", Toast.LENGTH_SHORT).show()
                        }
                        .setNeutralButton("I REPLACED IT") { _, _ ->
                            // Restore professional interval, clear audit, and reset wear
                            val updatedConsumable = consumable.copy(
                                isUnderAudit = false,
                                intervalKm = consumable.standardInterval,
                                lastReplacedKm = bike.odometer
                            )
                            bike.consumables[index] = updatedConsumable
                            refreshMaintenanceLists()
                            Toast.makeText(this, "TASK COMPLETED: ${consumable.partName}", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("NOT YET", null)
                        .show()
                }
            } 
            // LOGIC BRANCH 2: Item is a preventive maintenance task
            else if (consumable.isPreventive && consumable.diyReason != null) {
                itemView.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle(consumable.partName.uppercase())
                        .setMessage("${consumable.diyReason}\n\nAre you sure you want to mark this task as completed?")
                        .setPositiveButton("YES, COMPLETED") { _, _ ->
                            // Reset the task tracker to current mileage
                            val updatedConsumable = consumable.copy(lastReplacedKm = bike.odometer)
                            bike.consumables[index] = updatedConsumable
                            refreshMaintenanceLists()
                            Toast.makeText(this, "TASK COMPLETED: ${consumable.partName}", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("NOT YET", null)
                        .show()
                }
            } 
            // LOGIC BRANCH 3: Item is a standard lifecycle component
            else {
                itemView.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setTitle("REPLACE ${consumable.partName.uppercase()}")
                        .setMessage("Have you replaced this component?")
                        .setPositiveButton("YES") { _, _ ->
                            // Reset the wear tracker to current mileage
                            val updatedConsumable = consumable.copy(lastReplacedKm = bike.odometer)
                            bike.consumables[index] = updatedConsumable
                            refreshMaintenanceLists()
                            Toast.makeText(this, "UNIT REPLACED: ${consumable.partName}", Toast.LENGTH_SHORT).show()
                        }
                        .setNegativeButton("NO", null)
                        .show()
                }
            }

            // Display historical mileage data
            itemView.findViewById<TextView>(R.id.text_last_replaced).text = "${consumable.lastReplacedKm} KM"
            itemView.findViewById<TextView>(R.id.text_interval).text = "${consumable.intervalKm} KM"

            val statusText = itemView.findViewById<TextView>(R.id.text_status)
            val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_wear)
            
            // MATH: Calculate how much mileage has been used since last service
            val kmSinceReplacement = bike.odometer - consumable.lastReplacedKm
            // MATH: Calculate percentage (capped at 100%)
            val progressPercent = ((kmSinceReplacement / consumable.intervalKm) * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progressPercent

            // UI LOGIC: Determine status label and background color
            if (kmSinceReplacement >= consumable.intervalKm) {
                if (consumable.isUnderAudit) {
                    // State: Safety Audit Triggered
                    statusText.text = "INSPECT NOW"
                    statusText.setTextColor(getColor(android.R.color.white))
                    statusText.backgroundTintList = ColorStateList.valueOf(getColor(R.color.industrial_orange))
                } else {
                    // State: Replacement/Service Due
                    statusText.text = if (consumable.isPreventive) "SERVICE NOW" else "REPLACE NOW"
                    statusText.setTextColor(getColor(android.R.color.white))
                    statusText.backgroundTintList = ColorStateList.valueOf(getColor(R.color.industrial_red))
                }
            } else {
                // State: Normal Operation
                statusText.text = "STABLE"
                statusText.setTextColor(getColor(android.R.color.white))
                statusText.backgroundTintList = ColorStateList.valueOf(getColor(R.color.industrial_green))
            }

            // Add the populated row to the appropriate container
            if (consumable.isPreventive) {
                preventiveContainer.addView(itemView)
            } else {
                lifecycleContainer.addView(itemView)
            }
        }
    }
}
