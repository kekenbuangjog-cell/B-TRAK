package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * MainActivity serves as the primary dashboard of the B-Trak application.
 * Now integrated with Room Database for persistent storage.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: BikeAdapter
    private lateinit var recyclerView: RecyclerView
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        recyclerView = findViewById(R.id.recycler_garage)

        val btnAddBike = findViewById<LinearLayout>(R.id.box_add_bike)
        btnAddBike.setOnClickListener {
            val intent = Intent(this, RegisterBikeActivity::class.java)
            intent.putExtra("USER_ID", userId)
            startActivity(intent)
        }

        val btnSettings = findViewById<ImageView>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val btnQuit = findViewById<ImageView>(R.id.btn_quit)
        btnQuit.setOnClickListener {
            showShutdownDialog()
        }
    }

    private fun showShutdownDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("SYSTEM SHUTDOWN")
            .setMessage("Are you sure you want to close and log out?")
            .setPositiveButton("CONFIRM") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadBikes()
    }

    private fun loadBikes() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@MainActivity)
            val bikeEntities = db.bikeDao().getBikesForUser(userId)
            
            // Map entities to domain models for the adapter
            val bikes = bikeEntities.map { entity ->
                Bike(
                    id = entity.id,
                    name = entity.name,
                    type = entity.type,
                    odometer = entity.odometer,
                    iconResId = entity.iconResId,
                    isSecondHand = entity.isSecondHand
                )
            }
            
            // Update GarageManager for other activities to use
            GarageManager.myGarage.clear()
            GarageManager.myGarage.addAll(bikes)

            adapter = BikeAdapter(GarageManager.myGarage)
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }
}