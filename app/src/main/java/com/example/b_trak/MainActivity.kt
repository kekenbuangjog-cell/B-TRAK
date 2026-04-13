package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    // 1. Define the Valet and the List here
    private lateinit var adapter: BikeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 2. Find the RecyclerView in your XML
        recyclerView = findViewById(R.id.recycler_garage)

        // 3. Setup the "+" button
        val btnAddBike = findViewById<LinearLayout>(R.id.box_add_bike)
        btnAddBike.setOnClickListener {
            val intent = Intent(this, RegisterBikeActivity::class.java)
            startActivity(intent)
        }
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