package com.example.b_trak

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * UNIT_REGISTRATION: Protocol for adding new bicycle hardware to the garage.
 * Facilitates the creation of Bike data models and their injection into the global state.
 */
class RegisterBikeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bike)

        // UI_BINDING: Connect all input components for unit registration
        val editBikeName = findViewById<EditText>(R.id.edit_bike_name)
        val spinnerType = findViewById<Spinner>(R.id.spinner_bike_type)
        val btnAdd = findViewById<Button>(R.id.btn_add_to_garage)

        // ACTION: Process Unit Entry
        btnAdd.setOnClickListener {
            val name = editBikeName.text.toString().trim()
            val typeValue = spinnerType.selectedItem.toString()
            val typePosition = spinnerType.selectedItemPosition

            // PROTOCOL: Input Validation
            if (name.isEmpty()) {
                Toast.makeText(this, "ERROR: PLEASE ENTER BIKE NAME", Toast.LENGTH_SHORT).show()
            }
            else if (typePosition == 0) {
                Toast.makeText(this, "ERROR: PLEASE SELECT A TYPE", Toast.LENGTH_SHORT).show()
            }
            else {
                // DATA_PASSING: Direct injection into the Singleton GarageManager list.
                // This updates the global state across the entire application lifecycle.
                GarageManager.myGarage.add(Bike(name, typeValue))

                // STATUS: Operation Confirmation
                val successMessage = "DETAILS: $name ($typeValue)\nADD TO GARAGE SUCCESS!"
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()

                // NAVIGATION: Return to the Primary Hub (MainActivity)
                finish()
            }
        }
    }
}