package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {

    // 1. Define the Valet and the List here
    private lateinit var adapter: BikeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Find the RecyclerView in your XML
        recyclerView = findViewById(R.id.recycler_garage)

        // 3. Setup the buttons
        val btnAddBike = findViewById<LinearLayout>(R.id.box_add_bike)
        btnAddBike.setOnClickListener {
            val intent = Intent(this, RegisterBikeActivity::class.java)
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
            .setTitle("SYSTEM_SHUTDOWN")
            .setMessage("Are you sure you want to close and log out?")
            .setPositiveButton("CONFIRM") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("CANCEL", null)
            .show()
    }

    override fun onResume() {
        super.onResume()

        // 4. Connect the data to the Valet and show it
        // We do this in onResume so it refreshes after you add a new bike
        adapter = BikeAdapter(GarageManager.myGarage)
        recyclerView.adapter = adapter

        // Tell the list to refresh its layout
        adapter.notifyDataSetChanged()
    }
}