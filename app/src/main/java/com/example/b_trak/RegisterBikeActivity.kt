package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * RegisterBikeActivity manages the workflow for adding a new bicycle to the user's garage.
 * It handles user input validation, bicycle type selection, and the initialization of the Bike data model.
 */
class RegisterBikeActivity : AppCompatActivity() {

    /**
     * Sets up the registration form, including the bike type spinner and the submission logic.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bind the Activity to its registration form XML layout
        setContentView(R.layout.activity_register_bike)

        // UI BINDING: Connect input fields, spinner, and checkboxes from the layout
        val editBikeName = findViewById<EditText>(R.id.edit_bike_name)
        val spinnerType = findViewById<Spinner>(R.id.spinner_bike_type)
        val checkIsSecondHand = findViewById<android.widget.CheckBox>(R.id.check_is_second_hand)
        val btnAdd = findViewById<Button>(R.id.btn_add_to_garage)

        // SPINNER SETUP: Load the predefined bike types from the strings.xml resource file
        val bikeTypes = resources.getStringArray(R.array.bike_types)
        // Create an adapter to map the array data to the spinner's visual items
        val adapter = ArrayAdapter(this, R.layout.spinner_item, bikeTypes)
        // Set the layout used for the dropdown list items
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        // Assign the adapter to the spinner component
        spinnerType.adapter = adapter

        // SUBMISSION ACTION: Logic triggered when the user taps the "ADD TO GARAGE" button
        btnAdd.setOnClickListener {
            // Retrieve and trim user input from the name field
            val name = editBikeName.text.toString().trim()
            // Retrieve the selected bicycle discipline category
            val typeValue = spinnerType.selectedItem.toString()
            // Get the index of the selected item to check for the "[ SELECT TYPE ]" placeholder
            val typePosition = spinnerType.selectedItemPosition
            // Check the status of the second-hand bike checkbox
            val isSecondHand = checkIsSecondHand.isChecked

            // VALIDATION: Ensure the user has entered a name
            if (name.isEmpty()) {
                Toast.makeText(this, "ERROR: PLEASE ENTER BIKE NAME", Toast.LENGTH_SHORT).show()
            }
            // VALIDATION: Ensure the user has selected a valid bike type (not the placeholder at index 0)
            else if (typePosition == 0) {
                Toast.makeText(this, "ERROR: PLEASE SELECT A TYPE", Toast.LENGTH_SHORT).show()
            }
            else {
                // ICON MAPPING: Select a discipline-specific icon based on the user's type selection
                val iconRes = when (typeValue) {
                    "MTB XC (Cross Country)" -> R.drawable.ic_mountain_bike
                    "ROAD AERO (Racing)" -> R.drawable.ic_road_bike
                    "FIXED GEAR (City/Track)" -> R.drawable.ic_fixed_gear
                    "GRAVEL EXPLORER (Adventure)" -> R.drawable.ic_gravel_explorer
                    "BMX STREET (Trick/Park)" -> R.drawable.ic_bmx_street
                    "COMMUTER BIKE(Ordinary)" -> R.drawable.ic_commuter_bike
                    else -> R.drawable.logo_placeholder // Default fallback icon
                }

                // DATA CREATION: Instantiate a new Bike object with the gathered information
                // The odometer defaults to 0.0 for new registrations.
                val newBike = Bike(name, typeValue, 0.0, iconRes, isSecondHand = isSecondHand)
                
                // DATA INJECTION: Add the newly created bike directly into the global Singleton list
                GarageManager.myGarage.add(newBike)

                // FEEDBACK: Notify the user that the registration was successful
                val successMessage = "DETAILS: $name ($typeValue)\nADD TO GARAGE SUCCESS!"
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()

                // NAVIGATION: Close this activity and return to the MainActivity (Dashboard)
                finish()
            }
        }
    }
}