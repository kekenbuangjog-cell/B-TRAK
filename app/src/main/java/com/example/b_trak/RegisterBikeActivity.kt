package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * RegisterBikeActivity manages the workflow for adding a new bicycle to the user's garage.
 * Now persists data to Room Database.
 */
class RegisterBikeActivity : AppCompatActivity() {

    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bike)

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        val editBikeName = findViewById<EditText>(R.id.edit_bike_name)
        val spinnerType = findViewById<Spinner>(R.id.spinner_bike_type)
        val checkIsSecondHand = findViewById<android.widget.CheckBox>(R.id.check_is_second_hand)
        val btnAdd = findViewById<Button>(R.id.btn_add_to_garage)

        val bikeTypes = resources.getStringArray(R.array.bike_types)
        val adapter = ArrayAdapter(this, R.layout.spinner_item, bikeTypes)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerType.adapter = adapter

        btnAdd.setOnClickListener {
            val name = editBikeName.text.toString().trim()
            val typeValue = spinnerType.selectedItem.toString()
            val typePosition = spinnerType.selectedItemPosition
            val isSecondHand = checkIsSecondHand.isChecked

            if (name.isEmpty()) {
                Toast.makeText(this, "ERROR: PLEASE ENTER BIKE NAME", Toast.LENGTH_SHORT).show()
            } else if (typePosition == 0) {
                Toast.makeText(this, "ERROR: PLEASE SELECT A TYPE", Toast.LENGTH_SHORT).show()
            } else {
                val iconRes = when (typeValue) {
                    "MTB XC (Cross Country)" -> R.drawable.ic_mountain_bike
                    "ROAD AERO (Racing)" -> R.drawable.ic_road_bike
                    "FIXED GEAR (City/Track)" -> R.drawable.ic_fixed_gear
                    "GRAVEL EXPLORER (Adventure)" -> R.drawable.ic_gravel_explorer
                    "BMX STREET (Trick/Park)" -> R.drawable.ic_bmx_street
                    "COMMUTER BIKE(Ordinary)" -> R.drawable.ic_commuter_bike
                    else -> R.drawable.logo_placeholder
                }

                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(this@RegisterBikeActivity)
                    
                    val bikeEntity = BikeEntity(
                        userId = userId,
                        name = name,
                        type = typeValue,
                        odometer = 0.0,
                        iconResId = iconRes,
                        isSecondHand = isSecondHand
                    )
                    
                    val bikeId = db.bikeDao().insertBike(bikeEntity)
                    
                    // Add default consumables
                    val consumables = createDefaultConsumables(bikeId, 0.0, isSecondHand)
                    db.consumableDao().insertConsumables(consumables)

                    Toast.makeText(this@RegisterBikeActivity, "BIKE ADDED TO GARAGE", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun createDefaultConsumables(bikeId: Long, odometer: Double, isSecondHand: Boolean): List<ConsumableEntity> {
        val entities = mutableListOf<ConsumableEntity>()
        
        val lifecycleParts = listOf(
            Triple("Drive Chain", 3000.0, "Drive Chain"),
            Triple("Front Tire", 5000.0, "Front Tire"),
            Triple("Rear Tire", 5000.0, "Rear Tire"),
            Triple("Front Brake Pads", 2500.0, "Front Brake Pads"),
            Triple("Rear Brake Pads", 2500.0, "Rear Brake Pads")
        )

        for (part in lifecycleParts) {
            entities.add(ConsumableEntity(
                bikeId = bikeId,
                partName = part.first,
                lastReplacedKm = odometer,
                intervalKm = if (isSecondHand) 50.0 else part.second,
                isPreventive = false,
                diyReason = null,
                isUnderAudit = isSecondHand,
                standardInterval = part.second
            ))
        }
        
        val preventiveTasks = listOf(
            Triple("Drivetrain Lube", 150.0, "Regular lubrication minimizes friction, prevents annoying squeaks, and protects against rust."),
            Triple("Safety Bolt Check", 500.0, "Vibrations from riding can loosen critical bolts over time."),
            Triple("Deep Clean/Degrease", 200.0, "A thorough wash and degrease removes abrasive grit from your moving parts."),
            Triple("Brake Alignment", 1500.0, "Brake cables stretch and pads wear down with use.")
        )

        for (task in preventiveTasks) {
            entities.add(ConsumableEntity(
                bikeId = bikeId,
                partName = task.first,
                lastReplacedKm = odometer,
                intervalKm = task.second,
                isPreventive = true,
                diyReason = task.third,
                isUnderAudit = false,
                standardInterval = task.second
            ))
        }
        
        return entities
    }
}