package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * MainActivity serves as the primary dashboard of the B-Trak application.
 * It displays the list of bicycles currently in the garage and provides entry points for adding 
 * new bikes, accessing settings, or exiting the application.
 */
class MainActivity : AppCompatActivity() {

    // Declare the adapter that will manage the data-to-view mapping for the bike list
    private lateinit var adapter: BikeAdapter
    // Declare the RecyclerView which is the container for the list items
    private lateinit var recyclerView: RecyclerView

    /**
     * Initializes the dashboard UI and configures the navigation buttons.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind the Activity to the main dashboard XML layout
        setContentView(R.layout.activity_main)

        // Find the RecyclerView component within the XML layout by its ID
        recyclerView = findViewById(R.id.recycler_garage)

        // Find the "Add Bike" box (wrapped in a LinearLayout) and set its click listener
        val btnAddBike = findViewById<LinearLayout>(R.id.box_add_bike)
        btnAddBike.setOnClickListener {
            // Intent to navigate the user to the bike registration screen
            val intent = Intent(this, RegisterBikeActivity::class.java)
            startActivity(intent)
        }

        // Find the settings gear icon and set its click listener
        val btnSettings = findViewById<ImageView>(R.id.btn_settings)
        btnSettings.setOnClickListener {
            // Intent to navigate the user to the settings screen
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        // Find the exit/power icon and set its click listener
        val btnQuit = findViewById<ImageView>(R.id.btn_quit)
        btnQuit.setOnClickListener {
            // Trigger the custom shutdown confirmation dialog
            showShutdownDialog()
        }
    }

    /**
     * Displays a Material-themed confirmation dialog before closing the application.
     * This ensures users don't accidentally exit and lose their current session data.
     */
    private fun showShutdownDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("SYSTEM SHUTDOWN") // Industrial-themed title
            .setMessage("Are you sure you want to close and log out?") // Safety confirmation message
            .setPositiveButton("CONFIRM") { _, _ ->
                // Closes all activities in the stack and terminates the application process
                finishAffinity()
            }
            .setNegativeButton("CANCEL", null) // Dismisses the dialog without taking action
            .show()
    }

    /**
     * Called every time the Activity comes back to the foreground.
     * This is the ideal place to refresh the list, ensuring that any new bikes added via
     * RegisterBikeActivity appear immediately.
     */
    override fun onResume() {
        super.onResume()

        // Create a new instance of the adapter, passing in the global list of bikes from the GarageManager
        adapter = BikeAdapter(GarageManager.myGarage)
        // Assign the adapter to the RecyclerView to begin populating the list
        recyclerView.adapter = adapter

        // Explicitly notify the adapter that the underlying data set might have changed
        // This triggers a UI redraw of the list items
        adapter.notifyDataSetChanged()
    }
}