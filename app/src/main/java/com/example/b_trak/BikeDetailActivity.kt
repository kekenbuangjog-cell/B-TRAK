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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * BikeDetailActivity handles bike maintenance status and persists changes to Room.
 */
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

        // Load full data from DB to ensure we have the consumables with IDs
        loadBikeData()
    }

    private fun loadBikeData() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@BikeDetailActivity)
            val consumableEntities = db.consumableDao().getConsumablesForBike(bike.id)
            
            bike.consumables.clear()
            bike.consumables.addAll(consumableEntities.map { entity ->
                Consumable(
                    id = entity.id,
                    partName = entity.partName,
                    lastReplacedKm = entity.lastReplacedKm,
                    intervalKm = entity.intervalKm,
                    isPreventive = entity.isPreventive,
                    diyReason = entity.diyReason,
                    isUnderAudit = entity.isUnderAudit,
                    standardInterval = entity.standardInterval
                )
            })
            
            setupUI()
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

        refreshMaintenanceLists()
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
                    if (newValue < bike.odometer) {
                        Toast.makeText(this, "ERROR: ODOMETER CANNOT GO BACKWARDS", Toast.LENGTH_SHORT).show()
                    } else {
                        bike.odometer = newValue
                        findViewById<TextView>(R.id.detail_total_km).text = bike.odometer.toString()
                        
                        // Sync to DB
                        lifecycleScope.launch {
                            val db = AppDatabase.getDatabase(this@BikeDetailActivity)
                            
                            // Let's just update the odometer in DB using a custom query if needed, 
                            // but for now, I'll fetch the bike to get its userId.
                            // Wait, I don't have a getBikeById. I'll add it.
                            db.bikeDao().updateBikeOdometer(bike.id, bike.odometer)
                            
                            refreshMaintenanceLists()
                        }
                    }
                }
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    private fun updateConsumableInDb(consumable: Consumable) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@BikeDetailActivity)
            val entity = ConsumableEntity(
                id = consumable.id,
                bikeId = bike.id,
                partName = consumable.partName,
                lastReplacedKm = consumable.lastReplacedKm,
                intervalKm = consumable.intervalKm,
                isPreventive = consumable.isPreventive,
                diyReason = consumable.diyReason,
                isUnderAudit = consumable.isUnderAudit,
                standardInterval = consumable.standardInterval
            )
            // Need an update method in ConsumableDao
            db.consumableDao().updateConsumable(entity)
            refreshMaintenanceLists()
        }
    }

    private fun refreshMaintenanceLists() {
        val lifecycleContainer = findViewById<LinearLayout>(R.id.lifecycle_container)
        val preventiveContainer = findViewById<LinearLayout>(R.id.preventive_container)

        lifecycleContainer.removeAllViews()
        preventiveContainer.removeAllViews()

        val inflater = LayoutInflater.from(this)

        for ((index, consumable) in bike.consumables.withIndex()) {
            val itemView = inflater.inflate(R.layout.item_consumable, (if (consumable.isPreventive) preventiveContainer else lifecycleContainer), false)
            
            val nameText = itemView.findViewById<TextView>(R.id.text_part_name)
            nameText.text = consumable.partName.uppercase()
            
            itemView.setOnClickListener {
                val title = if (consumable.isUnderAudit) "INSPECT ${consumable.partName.uppercase()}"
                            else if (consumable.isPreventive) consumable.partName.uppercase()
                            else "REPLACE ${consumable.partName.uppercase()}"
                
                val message = if (consumable.isUnderAudit) "This is a second-hand component. Have you checked it properly?"
                              else if (consumable.isPreventive) "${consumable.diyReason}\n\nMark as completed?"
                              else "Have you replaced this component?"

                AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("YES") { _, _ ->
                        val updated = consumable.copy(
                            isUnderAudit = false,
                            intervalKm = if (consumable.isUnderAudit) consumable.standardInterval else consumable.intervalKm,
                            lastReplacedKm = bike.odometer
                        )
                        bike.consumables[index] = updated
                        updateConsumableInDb(updated)
                    }
                    .setNegativeButton("NO", null)
                    .show()
            }

            itemView.findViewById<TextView>(R.id.text_last_replaced).text = "${consumable.lastReplacedKm} KM"
            itemView.findViewById<TextView>(R.id.text_interval).text = "${consumable.intervalKm} KM"

            val statusText = itemView.findViewById<TextView>(R.id.text_status)
            val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_wear)
            
            val kmSinceReplacement = bike.odometer - consumable.lastReplacedKm
            val progressPercent = ((kmSinceReplacement / consumable.intervalKm) * 100).toInt().coerceIn(0, 100)
            progressBar.progress = progressPercent

            if (kmSinceReplacement >= consumable.intervalKm) {
                if (consumable.isUnderAudit) {
                    statusText.text = "INSPECT NOW"
                    statusText.backgroundTintList = ColorStateList.valueOf(getColor(R.color.industrial_orange))
                } else {
                    statusText.text = if (consumable.isPreventive) "SERVICE NOW" else "REPLACE NOW"
                    statusText.backgroundTintList = ColorStateList.valueOf(getColor(R.color.industrial_red))
                }
            } else {
                statusText.text = "STABLE"
                statusText.backgroundTintList = ColorStateList.valueOf(getColor(R.color.industrial_green))
            }

            if (consumable.isPreventive) preventiveContainer.addView(itemView) else lifecycleContainer.addView(itemView)
        }
    }
}
