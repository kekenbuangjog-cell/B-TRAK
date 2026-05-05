package com.example.b_trak

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class BikeDetailActivity : AppCompatActivity() {

    private var bikeIndex: Int = -1
    private lateinit var bike: Bike

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bike_details)

        bikeIndex = intent.getIntExtra("BIKE_INDEX", -1)
        if (bikeIndex == -1 || bikeIndex >= GarageManager.myGarage.size) {
            finish()
            return
        }

        bike = GarageManager.myGarage[bikeIndex]

        ensureDefaultConsumables()
        setupUI()
    }

    private fun ensureDefaultConsumables() {
        // Only initialize if the list is empty (first time viewing this bike)
        if (bike.consumables.isEmpty()) {
            val defaults = listOf(
                Consumable("Drive Chain", bike.odometer, 3000.0),
                Consumable("Front Tire", bike.odometer, 5000.0),
                Consumable("Rear Tire", bike.odometer, 5000.0),
                Consumable("Front Brake Pads", bike.odometer, 2500.0),
                Consumable("Rear Brake Pads", bike.odometer, 2500.0)
            )
            bike.consumables.addAll(defaults)
        }
    }

    private fun setupUI() {
        findViewById<TextView>(R.id.detail_bike_name).text = bike.name
        findViewById<TextView>(R.id.detail_bike_type).text = bike.type
        findViewById<TextView>(R.id.detail_total_km).text = bike.odometer.toString()
        findViewById<android.widget.ImageView>(R.id.detail_bike_icon).setImageResource(bike.iconResId)

        findViewById<Button>(R.id.btn_update_odometer).setOnClickListener {
            showUpdateOdometerDialog()
        }

        refreshConsumables()
    }

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
                    bike.odometer = newValue
                    findViewById<TextView>(R.id.detail_total_km).text = bike.odometer.toString()
                    refreshConsumables() 
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun refreshConsumables() {
        // Map the fixed part names to their respective included layout IDs
        val partToViewId = mapOf(
            "Drive Chain" to R.id.consumable_chain,
            "Front Tire" to R.id.consumable_front_tire,
            "Rear Tire" to R.id.consumable_rear_tire,
            "Front Brake Pads" to R.id.consumable_front_brake,
            "Rear Brake Pads" to R.id.consumable_rear_brake
        )

        for (consumable in bike.consumables) {
            val viewId = partToViewId[consumable.partName] ?: continue
            val itemView = findViewById<android.view.View>(viewId)
            
            itemView.findViewById<TextView>(R.id.text_part_name).text = consumable.partName.uppercase()
            itemView.findViewById<TextView>(R.id.text_last_replaced).text = "${consumable.lastReplacedKm} KM"
            itemView.findViewById<TextView>(R.id.text_interval).text = "${consumable.intervalKm} KM"

            val statusText = itemView.findViewById<TextView>(R.id.text_status)
            val progressBar = itemView.findViewById<android.widget.ProgressBar>(R.id.progress_wear)
            val kmSinceReplacement = bike.odometer - consumable.lastReplacedKm
            
            val progressPercent = ((kmSinceReplacement / consumable.intervalKm) * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progressPercent

            if (kmSinceReplacement >= consumable.intervalKm) {
                statusText.text = "REPLACE_NOW"
                statusText.setTextColor(getColor(android.R.color.white))
                statusText.setBackgroundResource(R.drawable.industrial_input)
                statusText.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_dark))
            } else {
                statusText.text = "STABLE"
                statusText.setTextColor(getColor(R.color.industrial_orange))
                statusText.setBackgroundResource(R.drawable.industrial_input)
                statusText.backgroundTintList = android.content.res.ColorStateList.valueOf(getColor(R.color.industrial_destructive_bg))
            }
        }
    }
}