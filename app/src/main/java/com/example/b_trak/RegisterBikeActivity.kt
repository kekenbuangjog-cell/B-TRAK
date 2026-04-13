package com.example.b_trak

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterBikeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bike)

        val editBikeName = findViewById<EditText>(R.id.edit_bike_name)
        val spinnerType = findViewById<Spinner>(R.id.spinner_bike_type)
        val btnAdd = findViewById<Button>(R.id.btn_add_to_garage)

        btnAdd.setOnClickListener {
            val name = editBikeName.text.toString().trim()
            val typeValue = spinnerType.selectedItem.toString()
            val typePosition = spinnerType.selectedItemPosition

            // 1. Validation Logic
            if (name.isEmpty()) {
                Toast.makeText(this, "ERROR: PLEASE ENTER BIKE NAME", Toast.LENGTH_SHORT).show()
            }
            else if (typePosition == 0) {
                Toast.makeText(this, "ERROR: PLEASE SELECT A TYPE", Toast.LENGTH_SHORT).show()
            }
            else {
                // 2. SUCCESS: Save to your In-Memory list (GarageManager)
                GarageManager.myGarage.add(Bike(name, typeValue))

                // 3. The specific details message you requested
                val successMessage = "DETAILS: $name ($typeValue)\nADD TO GARAGE SUCCESS!"
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()

                // 4. Force screen back to MainActivity
                finish()
            }
        }
    }
}