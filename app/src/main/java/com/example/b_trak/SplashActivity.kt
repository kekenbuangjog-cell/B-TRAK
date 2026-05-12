package com.example.b_trak

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

/**
 * SplashActivity serves as the initial entry point of the application.
 * It provides a simulated "boot sequence" UI with a progress bar before transitioning the user
 * to the authentication screen. This creates the app's industrial aesthetic from the start.
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Called when the activity is starting.
     * Sets up the edge-to-edge UI layout and triggers the loading simulation.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge() // Expand view to device borders for a modern look
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Bind the XML layout file

        // Connect the visual progress components from the XML layout to Kotlin variables
        val progressBar = findViewById<ProgressBar>(R.id.splash_progress)
        val statusText = findViewById<TextView>(R.id.txt_loading_status)

        // Instantiate a Handler tied to the main (UI) thread to safely update UI components from a background process
        val handler = Handler(Looper.getMainLooper())

        // Launch a separate background thread to handle the time-consuming simulation
        // This prevents the main UI thread from locking up or freezing during the loop
        Thread {
            // Loop from 0 to 100 to simulate a percentage completion
            for (i in 0..100) {
                // Pause the background thread for 35 milliseconds per iteration
                Thread.sleep(35) 
                
                // Post a message back to the main UI thread to update the visual components
                handler.post {
                    // Update the visual progress bar fill
                    progressBar.progress = i
                    // Update the text label to show the current percentage
                    statusText.text = "AUTHENTICATING KERNEL... $i%"

                    // Check if the simulation has reached 100%
                    if (i == 100) {
                        // Create an Intent indicating the system should switch to LoginActivity
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent) // Execute the transition

                        // Terminate the SplashActivity. This removes it from the back-stack,
                        // ensuring the user cannot press the Android 'Back' button and return to the loading screen.
                        finish()
                    }
                }
            }
        }.start() // Begin execution of the thread immediately
    }
}